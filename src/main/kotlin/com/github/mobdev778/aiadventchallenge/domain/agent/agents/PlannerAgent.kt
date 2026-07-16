package com.github.mobdev778.aiadventchallenge.domain.agent.agents

import com.github.mobdev778.aiadventchallenge.data.settings.repository.SettingsRepository
import com.github.mobdev778.aiadventchallenge.domain.agent.AgentOrchestrator
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentContext
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentRequest
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentResponse
import com.github.mobdev778.aiadventchallenge.domain.chatclient.ChatClient
import com.github.mobdev778.aiadventchallenge.domain.invariant.InvariantRegistry
import com.github.mobdev778.aiadventchallenge.domain.mcpserver.McpServerInteractor
import com.github.mobdev778.aiadventchallenge.domain.task.TaskState
import kotlinx.coroutines.CoroutineScope

/**
 * Агент, отвечающий за этап планирования задачи.
 *
 * Наследует базовую функциональность [BaseAgent] и специализируется на планировании:
 * собирает требования, формирует план и контролирует переход к этапу выполнения.
 * Использует пользовательские правила, инварианты и инструменты MCP-серверов.
 *
 * @param id уникальный идентификатор агента.
 * @param invariantRegistry реестр инвариантов для проверки ответов.
 * @param settingsRepository репозиторий настроек (предоставляет синхронный доступ к настройкам).
 * @param chatClient клиент для взаимодействия с языковой моделью.
 * @param mcpServerInteractor интерактор для работы с MCP-серверами.
 * @param scope корутин-скоуп для асинхронных операций.
 */
class PlannerAgent(
    id: String,
    invariantRegistry: InvariantRegistry,
    settingsRepository: SettingsRepository,
    chatClient: ChatClient,
    mcpServerInteractor: McpServerInteractor,
    scope: CoroutineScope,
) : BaseAgent(id, invariantRegistry, settingsRepository, chatClient, mcpServerInteractor, scope) {

    /**
     * Возвращает системное правило, которое инструктирует агента добавить маркер завершения планирования.
     *
     * @return строка с инструкцией для LLM: после завершения этапа планирования вставить в ответ метку `[EXECUTION]`.
     */
    override suspend fun getAgentRules(): String {
        return "Верни в конце ответа [EXECUTION] если этап планирования полностью завершен."
    }

    /**
     * Обрабатывает запрос пользователя в контексте планирования задачи.
     *
     * Вызывает базовую обработку [BaseAgent.handle], затем анализирует ответ.
     * Если текстовый ответ содержит маркер `[EXECUTION]` и контекст задачи не равен `null`,
     * это означает, что планирование завершено, и агент переводит задачу в состояние
     * [TaskState.Execution], сбрасывает шаг на 1 и устанавливает описание `"Новая задача"`.
     * В противном случае исходный ответ возвращается без изменений.
     *
     * @param orchestrator оркестратор агентов, управляющий распределением запросов.
     * @param context текущий контекст агента.
     * @param request запрос, поступивший от пользователя.
     * @return ответ агента; если произошёл переход, то с обновлённым [TaskContext], иначе без изменений.
     */
    override suspend fun handle(
        orchestrator: AgentOrchestrator,
        context: AgentContext,
        request: AgentRequest
    ): AgentResponse {
        val response = super.handle(orchestrator, context, request)

        val taskContext = response.taskContext
        return when {
            taskContext == null -> response
            response.message.contains("[EXECUTION]") -> {
                response.copy(
                    taskContext = taskContext.copy(
                        state = TaskState.Execution,
                        step = 1,
                        current = "Новая задача"
                    )
                )
            }
            else -> response
        }
    }
}
