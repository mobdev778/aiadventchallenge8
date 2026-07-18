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
 * Агент валидации, отвечающий за проверку промежуточных и окончательных результатов выполнения задачи.
 *
 * Данный агент анализирует ответ языковой модели и, ориентируясь на специальные маркеры
 * (`[SUMMARIZE]` и `[EXECUTION]`), управляет переходами между стадиями жизненного цикла задачи.
 * Он может либо принять решение и перевести задачу в фазу вывода финального результата,
 * либо, если в процессе валидации обнаружены ошибки, инициировать повторную реализацию с исправлениями.
 *
 * Является частью системы агентов и расширяет {@link BaseAgent}, наследуя общую логику
 * взаимодействия с языковой моделью, реестром инвариантов и MCP-инструментами.
 *
 * @param id уникальный идентификатор агента в системе.
 * @param invariantRegistry реестр инвариантов, применяемых для валидации ответов.
 * @param settingsRepository репозиторий настроек приложения.
 * @param chatClient клиент для отправки запросов к языковой модели.
 * @param mcpServerInteractor интерактор MCP-серверов, предоставляющий доступ к инструментам.
 * @param scope корутин-скоп, в котором выполняются асинхронные операции агента.
 *
 * @see BaseAgent
 * @see AgentOrchestrator
 * @see TaskState
 */
@Suppress("LongParameterList")
class ValidatorAgent(
    id: String,
    invariantRegistry: InvariantRegistry,
    settingsRepository: SettingsRepository,
    chatClient: ChatClient,
    mcpServerInteractor: McpServerInteractor,
    scope: CoroutineScope,
) : BaseAgent(id, invariantRegistry, settingsRepository, chatClient, mcpServerInteractor, scope) {

    /**
     * Возвращает инструкции для языковой модели, определяющие логику поведения агента валидации.
     *
     * Модель получает указание добавлять в конец ответа специальные маркеры:
     * - `[SUMMARIZE]` — сигнализирует о завершении валидации и готовности к выводу финального результата;
     * - `[EXECUTION]` — указывает на обнаружение ошибок и необходимость повторной реализации решения.
     *
     * Эти маркеры используются методом {@link #handle(AgentOrchestrator, AgentContext, AgentRequest)} для
     * автоматического управления состоянием задачи.
     *
     * @return строка с правилами на русском языке для системного промпта.
     */
    override suspend fun getAgentRules(): String {
        return "Верни [SUMMARIZE] в конце ответа, если считаешь, что валидация решения завершена " +
                "и пользователю можно показать окончательное." +
                "Если текущее решение в ходе проверки оказалось неправильным (маловероятно, но все же) - " +
                "верни в конце [EXECUTION]."
    }

    /**
     * Обрабатывает запрос агента, выполняя валидацию ответа и обновляя контекст задачи.
     *
     * Вызывает базовую реализацию {@link BaseAgent#handle(AgentOrchestrator, AgentContext, AgentRequest)},
     * а затем анализирует текстовое сообщение ответа на наличие управляющих маркеров:
     * - при обнаружении `[SUMMARIZE]` переводит задачу в состояние {@link TaskState#PrintResult},
     *   устанавливая шаг `1` и текущее действие "Вывод финального решения";
     * - при обнаружении `[EXECUTION]` переключает задачу обратно в {@link TaskState#Execution},
     *   начиная новый шаг с описанием "Повторная реализация решения (с исправлениями)";
     * - если маркеры отсутствуют, возвращает исходный ответ без изменений.
     *
     * Если контекст задачи в ответе равен `null`, никакие изменения не производятся.
     *
     * @param orchestrator оркестратор агентов, управляющий пулами и маршрутизацией.
     * @param context текущий контекст выполнения агента.
     * @param request исходный запрос пользователя.
     * @return ответ агента ({@link AgentResponse}) с обновлённым контекстом задачи в зависимости от маркеров.
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
            response.message.contains("[SUMMARIZE]") -> {
                response.copy(
                    taskContext = taskContext.copy(
                        state = TaskState.PrintResult,
                        step = 1,
                        current = "Вывод финального решения"
                    )
                )
            }
            response.message.contains("[EXECUTION]") -> {
                response.copy(
                    taskContext = taskContext.copy(
                        state = TaskState.Execution,
                        step = 1,
                        current = "Повторная реализация решения (с исправлениями)"
                    )
                )
            }
            else -> response
        }
    }
}
