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
 * Агент-исполнитель, отвечающий за непосредственную реализацию решения задачи (стадия [TaskState.Execution]).
 *
 * Данный агент наследует всю базовую логику от [BaseAgent] и добавляет специфичную обработку
 * для этапа выполнения. Он способен генерировать код, создавать артефакты и взаимодействовать
 * с инструментами через [McpServerInteractor].
 *
 * Особенность поведения исполнителя заключается в том, что после получения ответа от LLM (через вызов
 * [BaseAgent.handle]) он анализирует текстовое сообщение на наличие специальных маркеров:
 * - **[VALIDATION]** — сигнализирует о завершении реализации и готовности перейти к проверке;
 * - **[PLANNING]** — указывает на неудовлетворительный результат, требующий возврата к этапу планирования.
 *
 * При обнаружении маркера агент модифицирует контекст задачи ([AgentResponse.taskContext]),
 * изменяя состояние и текущий шаг, чтобы оркестратор мог перенаправить задачу в соответствующий пул.
 *
 * @property id Уникальный идентификатор агента.
 * @property invariantRegistry Реестр инвариантов для валидации запросов/ответов.
 * @property settingsRepository Репозиторий настроек, влияющих на поведение агента.
 * @property chatClient Клиент для отправки запросов к языковой модели.
 * @property mcpServerInteractor Интерактор для взаимодействия с MCP-серверами (инструментами).
 * @property scope Корутин-скоуп, в котором исполняется асинхронная работа.
 */
@Suppress("LongParameterList")
class ExecutorAgent(
    id: String,
    invariantRegistry: InvariantRegistry,
    settingsRepository: SettingsRepository,
    chatClient: ChatClient,
    mcpServerInteractor: McpServerInteractor,
    scope: CoroutineScope,
) : BaseAgent(id, invariantRegistry, settingsRepository, chatClient, mcpServerInteractor, scope) {

    /**
     * Возвращает правила-инструкции для LLM, определяющие поведение на стадии выполнения.
     *
     * Эти инструкции добавляются к системному промпту и предписывают модели, что при успешном завершении
     * реализации она должна вставить строку `[VALIDATION]`, а при неудаче, требующей доработки требований, —
     * `[PLANNING]`.
     *
     * @return Строка с правилами, которая будет добавлена в запрос к модели.
     */
    override suspend fun getAgentRules(): String {
        return "Верни [VALIDATION], если считаешь, что реализация решения завершена " +
                "и можно перейти к проверке. " +
                "Верни [PLANNING], если считаешь, что реализация получилась плохой/неправильной " +
                "(маловероятно, но все же) и нужно доуточнить требования"
    }

    /**
     * Обрабатывает запрос пользователя в контексте текущей задачи, применяя логику стадии исполнения.
     *
     * Метод вызывает родительскую реализацию [BaseAgent.handle], которая формирует первичный ответ.
     * Затем анализирует текстовое сообщение ответа на предмет управляющих маркеров:
     * - При наличии `[VALIDATION]` переводит задачу в состояние [TaskState.Validation] и сбрасывает шаг на 1,
     *   устанавливая описание "Проверка решения".
     * - При наличии `[PLANNING]` возвращает задачу в состояние [TaskState.Planning] (новое планирование).
     * - Если маркеров нет, ответ возвращается без изменений.
     *
     * @param orchestrator Оркестратор агентов, через который можно инициировать дальнейшие действия.
     * @param context Контекст агента, содержащий профиль, историю, инварианты и доступные инструменты.
     * @param request Исходный запрос пользователя.
     * @return Объект [AgentResponse] с обновлённым контекстом задачи, отражающим переход между стадиями.
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
            response.message.contains("[VALIDATION]") -> {
                response.copy(
                    taskContext = taskContext.copy(
                        state = TaskState.Validation,
                        step = 1,
                        current = "Проверка решения"
                    )
                )
            }
            response.message.contains("[PLANNING]") -> {
                response.copy(
                    taskContext = taskContext.copy(
                        state = TaskState.Planning,
                        step = 1,
                        current = "Новое планирование"
                    )
                )
            }
            else -> response
        }
    }
}
