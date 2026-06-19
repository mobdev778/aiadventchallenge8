package com.github.mobdev778.aiadventchallenge.domain.chat

import com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.model.PlanningResponseDto
import com.github.mobdev778.aiadventchallenge.data.settings.repository.SettingsRepository
import com.github.mobdev778.aiadventchallenge.domain.chatclient.ChatClient
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Message
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Role
import com.github.mobdev778.aiadventchallenge.domain.task.TaskContext
import com.github.mobdev778.aiadventchallenge.domain.task.TaskState
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single
import org.koin.java.KoinJavaComponent.inject
import java.util.UUID

@Single
class TaskStateMachine(
    private val chatClient: ChatClient,
    private val settingsRepository: SettingsRepository,
) {

    private val json: Json by inject(Json::class.java)

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
                    state = TaskState.Execution,
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
                    state = TaskState.Execution,
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
     * Выполняет обновление контекста по ответу LLM.
     */
    suspend fun execute(context: TaskContext, response: String): TaskContext {
        // Если модель сигнализирует о завершении шага
        if (response.trim().contains("[next_step]")) {
            val nextStep = context.step + 1

            val updatedDone = context.done + context.current
            val nextCurrentAction = context.plan.getOrNull(nextStep - 1) ?: "Завершение"

            if (nextStep < context.plan.size) {
                // Возвращаем обновленный слой рабочей памяти
                return context.copy(
                    state = context.state,
                    step = nextStep,
                    done = updatedDone,
                    current = nextCurrentAction
                )
            }

            // особый кейс - все шаги текущего этапа завершены. Нужно получить от LLM новые шаги
            val nextState = when (context.state) {
                TaskState.Planning -> TaskState.Execution
                TaskState.Execution -> TaskState.Validation
                TaskState.Validation -> TaskState.PrintResult
                TaskState.PrintResult -> TaskState.Done
                TaskState.Done -> TaskState.Done
            }

            if (nextState == TaskState.PrintResult) {
                return context.copy(
                    state = TaskState.PrintResult,
                    step = context.plan.size - 1,
                    done = updatedDone,
                    current = "Работа над задачей завершена. Выведи пользователю финальное решение."
                )
            } else if (nextState == TaskState.Done) {
                return context.copy(
                    state = TaskState.Done,
                    step = context.plan.size - 1,
                    done = updatedDone,
                    current = "Работа над задачей завершена."
                )
            }
            return context.copy(
                state = nextState,
                step = context.plan.size - 1,
                done = updatedDone,
                current = nextCurrentAction
            )
        }

        // Если не next_step, контекст памяти не меняется
        return context
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