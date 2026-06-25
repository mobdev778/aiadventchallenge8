package com.github.mobdev778.aiadventchallenge.domain.agent.agents

import com.github.mobdev778.aiadventchallenge.data.settings.repository.SettingsRepository
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentContext
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentRequest
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentResponse
import com.github.mobdev778.aiadventchallenge.domain.agent.model.ToolResponse
import com.github.mobdev778.aiadventchallenge.domain.chat.model.ChatMessage
import com.github.mobdev778.aiadventchallenge.domain.chat.model.MessageType
import com.github.mobdev778.aiadventchallenge.domain.chatclient.ChatClient
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Message
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Role
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ToolCall
import com.github.mobdev778.aiadventchallenge.domain.invariant.InvariantRegistry
import com.github.mobdev778.aiadventchallenge.domain.invariant.ValidationResult
import com.github.mobdev778.aiadventchallenge.domain.mcpserver.McpServerInteractor
import com.github.mobdev778.aiadventchallenge.domain.task.TaskContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import java.util.UUID

abstract class BaseAgent(
    id: String,
    val invariantRegistry: InvariantRegistry,
    val settingsRepository: SettingsRepository,
    val chatClient: ChatClient,
    val mcpServerInteractor: McpServerInteractor,
    val scope: CoroutineScope,
) : Agent(id) {

    override suspend fun handle(
        context: AgentContext,
        request: AgentRequest
    ): AgentResponse {
        // 1. СБОРКА СИСТЕМНОГО ПРОМПТА ИЗ ТРЕХ СЛОЕВ ПАМЯТИ
        val systemPrompt = SystemPromptBuilder()
            .profile(context.profile)     // Долговременная память
            .context(context.taskContext!!) // Рабочая память
            .query(request.query)
            .invariants(context.invariants)
            .agentRules(getAgentRules())
            .build()

        // 2. Запрос к модели
        var response = sendMessage(
            context = context,
            systemPrompt = systemPrompt,
            request = request,
        )

        // 3. Валидация ответа через инварианты
        val validationResult = invariantRegistry.validate(request.query, response.message)
        if (validationResult is ValidationResult.Failed) {
            response = response.copy(message = "[Нарушение]: ${validationResult.reason}")
        }

        // 4. ОБНОВЛЕНИЕ РАБОЧЕЙ ПАМЯТИ
        // Передаем ответ в стейт-машину. Если там есть "next_step", рабочая память обновится
        val newTaskContext = context.taskContext?.let { currentContext ->
            updateContext(currentContext, response.message)
        }

        // 5. Возвращаем ответ
        return response.copy(
            taskContext = newTaskContext,
        )
    }

    abstract suspend fun getAgentRules(): String

    suspend fun sendMessage(
        context: AgentContext,
        systemPrompt: String,
        request: AgentRequest,
    ): AgentResponse {
        val userMessage = ChatMessage(
            id = UUID.randomUUID(),
            chatId = request.chatId,
            parentId = request.parentMessageId,
            branchB = false,
            time = System.currentTimeMillis(),
            text = request.query,
            type = MessageType.User,
            tokens = 0,
            rank = 0
        )

        // Инициализируем локальную историю сообщений для текущей сессии генерации
        val currentMessages = ArrayList<Message>()

        // Добавляем системный промт
        currentMessages.add(Message(Role.System, systemPrompt))

        // Переносим существующий контекст
        context.windowMessages.forEach { message ->
            val mapped = when (message.type) {
                MessageType.User -> Message(Role.User, content = message.text)
                MessageType.Bot -> Message(Role.Assistant, content = message.text)
                MessageType.Tool -> Message(
                    Role.Tool,
                    content = message.text,
                    toolCallId = message.toolCallId,
                    name = message.name
                )

                MessageType.StickyFacts -> Message(
                    Role.System,
                    content = "[CRITICAL_STICKY_FACTS]\nИспользуй следующие неизменяемые пары ключ-значение...\n${message.text}"
                )
            }
            currentMessages.add(mapped)
        }

        // Добавляем новое сообщение пользователя
        currentMessages.add(Message(Role.User, content = userMessage.text))

        val baseModel = settingsRepository.getSettings().baseModel.trim().replace("\n", "")
        val toolMessages = ArrayList<ToolResponse>()

        var totalPromptTokens = 0
        var totalCompletionTokens = 0
        var finalAnswer = "- no response -"

        var maxIterations = 5 // Защита от бесконечного цикла
        var shouldContinue = true

        try {
            while (shouldContinue && maxIterations > 0) {
                maxIterations--

                val response = chatClient.execute(
                    ChatRequest(
                        model = baseModel,
                        messages = currentMessages,
                        tools = context.tools,
                    ),
                )

                totalPromptTokens += response.usage?.promptTokens ?: 0
                totalCompletionTokens += response.usage?.completionTokens ?: 0

                val firstChoice = response.choices.firstOrNull()
                val assistantMessage = firstChoice?.message
                val toolCalls = assistantMessage?.toolCalls

                // 1. Обязательно добавляем ответ ассистента в историю (даже если там только tool_calls)
                val assistantMessageToHistory = Message(
                    role = Role.Assistant,
                    content = assistantMessage?.content,
                    toolCalls = toolCalls // Передаем tool_calls API-клиенту
                )
                currentMessages.add(assistantMessageToHistory)

                // 2. Проверяем, хочет ли модель вызвать инструменты
                if (!toolCalls.isNullOrEmpty()) {
                    println("!!! Вызов инструментов: $toolCalls")

                    val jobs = toolCalls.map { toolCall ->
                        scope.async {
                            // Важно: MCP может требовать тип "function", приводим к нужному формату
                            sendMcpMessage(toolCall.copy(type = "function"))
                        }
                    }

                    val results = jobs.awaitAll()

                    // 3. Добавляем результаты выполнения инструментов сразу после tool_calls
                    results.filterNotNull().forEach { toolResponse ->
                        toolMessages.add(toolResponse)

                        currentMessages.add(
                            Message(
                                role = Role.Tool,
                                content = toolResponse.content, // Результат работы функции
                                toolCallId = toolResponse.toolCallId, // Должен совпадать с id из toolCalls
                                name = toolResponse.name
                            )
                        )
                    }

                    // Цикл продолжается, отправляя обновленную историю обратно в LLM
                } else {
                    // Если вызовов инструментов больше нет, сохраняем финальный текст и выходим
                    finalAnswer = assistantMessage?.content?.takeIf { it.isNotBlank() } ?: "- no response -"
                    shouldContinue = false
                }
            }
        } catch (e: Exception) {
            finalAnswer = "Ошибка: ${e.message ?: e::class.java.simpleName}"
        }

        val responseMessage = ChatMessage(
            id = UUID.randomUUID(),
            chatId = userMessage.chatId,
            parentId = userMessage.id,
            branchB = false,
            time = System.currentTimeMillis(),
            text = finalAnswer,
            type = MessageType.Bot,
            tokens = totalCompletionTokens,
            rank = 0,
        )

        return AgentResponse(
            agent = this.toString(),
            request = request,
            toolMessages = emptyList(),
            message = responseMessage.text,
            requestTokens = totalPromptTokens,
            responseTokens = responseMessage.tokens,
            taskContext = null,
        )
    }

    /**
     * Выполняет обновление контекста по ответу LLM.
     */
    suspend fun updateContext(context: TaskContext, response: String): TaskContext = when {
        response.trim().contains("[next_step]") -> {
            val nextStep = context.step + 1

            val updatedDone = context.done + context.current
            val nextCurrentAction = context.plan.getOrNull(nextStep - 1) ?: "Завершение"

            // Возвращаем обновленный слой рабочей памяти
            context.copy(
                state = context.state,
                step = nextStep,
                done = updatedDone,
                current = nextCurrentAction
            )
        }

        else -> context
    }

    suspend fun sendMcpMessage(toolCall: ToolCall): ToolResponse? {
        return mcpServerInteractor.sendRequest(toolCall)
    }
}