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
 * Агент-суммаризатор, предназначенный для формирования итогового ответа пользователю в формате Markdown.
 * Наследуется от [BaseAgent], используя общую логику взаимодействия с LLM, инвариантами и MCP-инструментами.
 *
 * После получения ответа от базового агента анализирует его на наличие маркера `[DONE]`.
 * Если маркер присутствует, переводит контекст задачи ([TaskContext]) в состояние [TaskState.Done],
 * сигнализируя оркестратору о завершении задачи.
 *
 * @param id Уникальный идентификатор агента в системе.
 * @param invariantRegistry Реестр инвариантов для валидации запросов и ответов.
 * @param settingsRepository Репозиторий с настройками, влияющими на поведение агента.
 * @param chatClient Фасад для отправки запросов к языковой модели (LLM).
 * @param mcpServerInteractor Интерактор для вызова инструментов, предоставляемых MCP-серверами.
 * @param scope Контекст корутины, в котором будут запущены асинхронные операции агента.
 */
class SummarizerAgent(
    id: String,
    invariantRegistry: InvariantRegistry,
    settingsRepository: SettingsRepository,
    chatClient: ChatClient,
    mcpServerInteractor: McpServerInteractor,
    scope: CoroutineScope,
) : BaseAgent(id, invariantRegistry, settingsRepository, chatClient, mcpServerInteractor, scope) {

    /**
     * Возвращает правила поведения агента, которые будут добавлены в системный промпт.
     * Указывает агенту оформить финальное решение в формате Markdown и поместить маркер `[DONE]`
     * в конце сообщения после полного вывода решения.
     *
     * @return строка с инструкциями для LLM на русском языке.
     */
    override suspend fun getAgentRules(): String {
        return "Выведи пользователю финальное решение в формате Markdown. " +
                "Если решение полностью выведено, верни [DONE] в конце сообщения."
    }

    /**
     * Переопределённый обработчик запроса, расширяющий базовую логику [BaseAgent.handle].
     *
     * Сначала делегирует обработку родительскому классу, который выполняет полный цикл подготовки,
     * отправки запроса к LLM и валидации. Затем проверяет наличие маркера `[DONE]` в итоговом сообщении.
     * Если сообщение содержит маркер **и** контекст задачи не равен `null`,
     * обновляет контекст задачи, переводя его в состояние [TaskState.Done] и сбрасывая шаг к 1.
     * В остальных случаях возвращает ответ без изменений.
     *
     * @param orchestrator Оркестратор, управляющий пулами агентов. Не используется в текущей реализации.
     * @param context Контекст вызова, включающий профиль пользователя, историю и активные инструменты.
     * @param request Исходный запрос пользователя, обрабатываемый агентом.
     * @return [AgentResponse] с (возможно) обновлённым контекстом задачи.
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
            response.message.contains("[DONE]") -> {
                response.copy(
                    taskContext = taskContext.copy(
                        state = TaskState.Done,
                        step = 1,
                        current = "Задача завершена"
                    )
                )
            }
            else -> response
        }
    }
}
