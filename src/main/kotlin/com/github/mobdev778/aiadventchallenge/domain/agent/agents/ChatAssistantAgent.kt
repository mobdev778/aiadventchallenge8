package com.github.mobdev778.aiadventchallenge.domain.agent.agents

import com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.model.PlanningResponseDto
import com.github.mobdev778.aiadventchallenge.data.settings.repository.SettingsRepository
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentContext
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentRequest
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentResponse
import com.github.mobdev778.aiadventchallenge.domain.chatclient.ChatClient
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Message
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Role
import com.github.mobdev778.aiadventchallenge.domain.invariant.InvariantRegistry
import com.github.mobdev778.aiadventchallenge.domain.invariant.ValidationResult
import com.github.mobdev778.aiadventchallenge.domain.mcpserver.McpServerInteractor
import com.github.mobdev778.aiadventchallenge.domain.task.TaskContext
import com.github.mobdev778.aiadventchallenge.domain.task.TaskState
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.inject
import java.util.UUID
import kotlin.getValue

/**
 * Агент для поддержания обычной беседы.
 *
 * На каждом сообщении проверяет, озадачился ли пользователь выполнением большой задачи.
 * И если "видит", что задача создана, то создает контекст и планирует шаги.
 */
class ChatAssistantAgent(
    id: String,
    invariantRegistry: InvariantRegistry,
    settingsRepository: SettingsRepository,
    chatClient: ChatClient,
    mcpServerInteractor: McpServerInteractor,
    scope: CoroutineScope,
) : BaseAgent(id, invariantRegistry, settingsRepository, chatClient, mcpServerInteractor, scope) {

    private val json: Json by inject(Json::class.java)

    override suspend fun handle(
        context: AgentContext,
        request: AgentRequest
    ): AgentResponse {
        // 1. Генерируем начальны контекст
        var newTaskContext: TaskContext?
        if (context.taskContext?.state == TaskState.Done) {
            newTaskContext = recreateContext(context.taskContext, request.query)
        } else {
            newTaskContext = createContext(request.query)
        }

        // 2. СБОРКА СИСТЕМНОГО ПРОМПТА ИЗ ТРЕХ СЛОЕВ ПАМЯТИ
        val immutableTaskContext = newTaskContext

        val systemPrompt = if (newTaskContext != null) {
            // Если есть рабочая память, собираем динамический контекст
            SystemPromptBuilder()
                .profile(context.profile)     // Долговременная память
                .context(immutableTaskContext) // Рабочая память
                .query(request.query)
                .invariants(context.invariants)
                .agentRules(getAgentRules())
                .build()
        } else {
            // Если рабочей памяти нет, используем стандартный профиль
            "${context.profile.content}\nПользователь просто общается, помогай в свободном режиме."
        }

        // 3. Запрос к модели
        var response = sendMessage(
            context = context.copy(taskContext = newTaskContext),
            systemPrompt = systemPrompt,
            request = request,
        )

        // 4. Валидация ответа через инварианты
        val validationResult = invariantRegistry.validate(request.query, response.message)
        if (validationResult is ValidationResult.Failed) {
            response = response.copy(message = "[Нарушение]: ${validationResult.reason}")
        }

        // 5. ОБНОВЛЕНИЕ РАБОЧЕЙ ПАМЯТИ
        // Передаем ответ в стейт-машину. Если там есть "next_step", рабочая память обновится
        context.taskContext?.let { currentContext ->
            newTaskContext = updateContext(currentContext, response.message)
        }

        val taskContext = newTaskContext
        return when {
            taskContext == null -> response.copy(taskContext = newTaskContext)
            response.message.contains("[EXECUTION]") -> {
                response.copy(
                    taskContext = taskContext.copy(
                        state = TaskState.Execution,
                        step = 1,
                        current = taskContext.plan.firstOrNull() ?: "Новая задача"
                    )
                )
            }
            else -> response.copy(taskContext = newTaskContext)
        }
    }

    override suspend fun getAgentRules(): String {
        return "Верни [EXECUTION] в конце ответа, если считаешь, что этап планирования задачи можно пропустить"
    }

    /**
     * Создает начальный TaskContext.
     */
    suspend fun createContext(userQuery: String): TaskContext? {
        val planningPrompt = buildPlanningPrompt(userQuery)
        val baseModel = settingsRepository.getSettings().baseModel

        // Делаем быстрый скрытый запрос к LLM (без стриминга), чтобы понять интенцию
        val response = chatClient.execute(
            ChatRequest(
                model = baseModel,
                messages = listOf(Message(Role.System, planningPrompt)),
            )
        )
        val jsonResponse = response.choices.first().message.content ?: ""

        return try {
            val planResult: PlanningResponseDto = json.decodeFromString<PlanningResponseDto>(jsonResponse)
            if (planResult.isTask && planResult.taskName != null && planResult.plan != null) {
                // Инициализируем слой РАБОЧЕЙ ПАМЯТИ
                TaskContext(
                    id = UUID.randomUUID(),
                    task = planResult.taskName,
                    state = TaskState.Planning,
                    step = 1,
                    plan = planResult.plan,
                    done = emptyList(),
                    current = planResult.plan.firstOrNull() ?: "Начало задачи"
                )
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Создает новый TaskContext для повторного планирования по задаче
     */
    suspend fun recreateContext(context: TaskContext, userQuery: String): TaskContext? {
        val planningPrompt = buildReplanningPrompt(context, userQuery)
        val baseModel = settingsRepository.getSettings().baseModel

        val response = chatClient.execute(
            ChatRequest(
                model = baseModel,
                messages = listOf(Message(Role.System, planningPrompt)),
            )
        )
        val jsonResponse = response.choices.first().message.content ?: ""
        return try {
            val planResult: PlanningResponseDto = json.decodeFromString<PlanningResponseDto>(jsonResponse)
            if (planResult.isTask && planResult.taskName != null && planResult.plan != null) {
                // Инициализируем слой РАБОЧЕЙ ПАМЯТИ
                TaskContext(
                    id = UUID.randomUUID(),
                    task = planResult.taskName,
                    state = TaskState.Planning,
                    step = 1,
                    plan = planResult.plan,
                    done = emptyList(),
                    current = planResult.plan.firstOrNull() ?: "Начало задачи"
                )
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun buildPlanningPrompt(userQuery: String): String = """
        Ты — интеллектуальный планировщик задач. Твоя цель — проанализировать запрос пользователя и понять, 
        формулирует ли он задачу, требующую пошагового выполнения.
    
        Запрос пользователя: "$userQuery"
    
        ЕСЛИ ЗАПРОС НЕ ЯВЛЯЕТСЯ КОНКРЕТНОЙ ЗАДАЧЕЙ (например, это просто приветствие, флуд или общий вопрос):
        Верни строго: {"isTask": false}
    
        ЕСЛИ ЭТО ЗАДАЧА (например: "напиши фичу авторизации", "сделай ревью кода"):
        Разбей её на понятные, последовательные шаги (от 2 до 5 шагов).
        Верни ответ СТРОГО в формате JSON без лишнего текста и markdown-разметки:
        {
          "isTask": true,
          "taskName": "Краткое название задачи",
          "plan": ["Шаг 1...", "Шаг 2...", "Шаг 3..."]
        }
        """.trimIndent()

    private fun buildReplanningPrompt(context: TaskContext, userQuery: String): String = """
        Ты — интеллектуальный планировщик задач. Предыдущая задача была успешно ЗАВЕРШЕНА. 
        Сейчас пользователь прислал уточняющий запрос для запуска нового цикла планирования 
        (перепланирования) на основе результатов прошлой задачи.

        КОНТЕКСТ ПРОШЛОЙ ЗАДАЧИ:
        - Название: ${context.task}
        - Был утвержден план:
        ${context.plan}
        - Фактически выполнено:
        ${context.done}

        НОВЫЙ УТОЧНЯЮЩИЙ ЗАПРОС ПОЛЬЗОВАТЕЛЯ:
        "$userQuery

        Твоя цель — проанализировать новый запрос с учетом контекста прошлой задачи и понять, 
        формулирует ли пользователь новую конкретную задачу.

        ЕСЛИ ЗАПРОС НЕ ЯВЛЯЕТСЯ КОНКРЕТНОЙ ЗАДАЧЕЙ (например, это просто благодарность "спасибо", флуд или общий вопрос):
        Верни строго: {"isTask": false}

        ЕСЛИ ЭТО НОВАЯ ЗАДАЧА ИЛИ УТОЧНЕНИЕ (например: "добавь логирование в эту фичу", "напиши тесты для написанного кода"):
        Сформулируй новое название задачи и разбей её на понятные, последовательные шаги (от 2 до 5 шагов).
        Верни ответ СТРОГО в формате JSON без лишнего текста и markdown-разметки:
        {
          "isTask": true,
          "taskName": "Краткое название новой задачи",
          "plan": ["Шаг 1...", "Шаг 2...", "Шаг 3..."]
        }
    """.trimIndent()
}