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
                    state = TaskState.Execution, // Переходим к выполнению
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

    suspend fun execute(context: TaskContext, response: String): TaskContext {
        // Если модель сигнализирует о завершении шага
        if (response.trim().contains("next_step")) {
            val nextStep = context.step + 1

            // Логика перехода по этапам (TaskState)
            val newState = when {
                nextStep > context.plan.size -> TaskState.Done
                nextStep == context.plan.size -> TaskState.Validation
                else -> context.state // Остаемся в текущем, если это просто следующий шаг внутри Execution
            }

            // Обновляем списки сделанного и текущего
            val updatedDone = context.done + context.current
            val nextCurrentAction = if (newState != TaskState.Done) {
                context.plan.getOrNull(nextStep - 1) ?: "Завершение"
            } else {
                "Задача полностью выполнена"
            }

            // Возвращаем обновленный слой рабочей памяти
            return context.copy(
                state = newState,
                step = nextStep,
                done = updatedDone,
                current = nextCurrentAction
            )
        }

        // Если не next_step, контекст памяти не меняется
        return context
    }

    private fun buildPlanningPrompt(userQuery: String): String = """
        Ты — интеллектуальный планировщик задач. Твоя цель — проанализировать запрос пользователя и понять, формулирует ли он задачу, требующую пошагового выполнения.
    
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
}