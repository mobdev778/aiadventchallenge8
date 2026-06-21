package com.github.mobdev778.aiadventchallenge.domain.agent.agents

import com.github.mobdev778.aiadventchallenge.data.settings.repository.SettingsRepository
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentContext
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentRequest
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentResponse
import com.github.mobdev778.aiadventchallenge.domain.chat.model.ChatMessage
import com.github.mobdev778.aiadventchallenge.domain.chat.model.MessageType
import com.github.mobdev778.aiadventchallenge.domain.chatclient.ChatClient
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Message
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Role
import com.github.mobdev778.aiadventchallenge.domain.invariant.InvariantRegistry
import com.github.mobdev778.aiadventchallenge.domain.invariant.ValidationResult
import com.github.mobdev778.aiadventchallenge.domain.task.TaskContext
import java.util.UUID

abstract class BaseAgent(
    id: String,
    val invariantRegistry: InvariantRegistry,
    val settingsRepository: SettingsRepository,
    val chatClient: ChatClient,
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

        val requestMessages = (context.windowMessages + userMessage)
            .map { message ->
                when (message.type) {
                    MessageType.User -> {
                        Message(
                            role = Role.User,
                            content = message.text
                        )
                    }

                    MessageType.Bot -> {
                        Message(
                            role = Role.Assistant,
                            content = message.text
                        )
                    }

                    MessageType.StickyFacts -> Message(
                        role = Role.System,
                        content = "[CRITICAL_STICKY_FACTS]\nИспользуй следующие неизменяемые пары ключ-значение для контекста. " +
                                "Ты обязан строго следовать этим данным и не имеешь права их выдумывать или менять:" +
                                message.text
                    )
                }
            }

        val baseModel = settingsRepository.getSettings().baseModel
            .trim()
            .replace("\n", "")

        val (responseMessage, requestTokens) = try {
            val response = chatClient.execute(
                ChatRequest(
                    model = baseModel,
                    messages = listOf(Message(Role.System, systemPrompt)) + requestMessages,
                ),
            )
            val promptTokens = response.usage?.promptTokens ?: 0

            val responseTokens = response.usage?.completionTokens ?: 0
            val answer = response.choices.firstOrNull()?.message?.content
                ?.takeIf { it.isNotBlank() }
                ?: "- no response -"

            ChatMessage(
                id = UUID.randomUUID(),
                chatId = userMessage.chatId,
                parentId = userMessage.id,
                branchB = false,
                time = System.currentTimeMillis(),
                text = answer,
                type = MessageType.Bot,
                tokens = responseTokens,
                rank = 0,
            ) to promptTokens
        } catch (e: Exception) {
            ChatMessage(
                id = UUID.randomUUID(),
                chatId = userMessage.chatId,
                parentId = userMessage.id,
                branchB = false,
                time = System.currentTimeMillis(),
                text = "Ошибка: ${e.message ?: e::class.java.simpleName}",
                type = MessageType.Bot,
                tokens = 0,
                rank = 0,
            ) to 0
        }
        return AgentResponse(
            agent = this.toString(),
            request = request,
            message = responseMessage.text,
            requestTokens = requestTokens,
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
}