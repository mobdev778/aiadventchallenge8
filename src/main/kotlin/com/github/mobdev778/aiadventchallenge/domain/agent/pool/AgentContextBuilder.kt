package com.github.mobdev778.aiadventchallenge.domain.agent.pool

import com.github.mobdev778.aiadventchallenge.data.profile.repository.ProfileRepository
import com.github.mobdev778.aiadventchallenge.data.taskcontext.repository.TaskContextRepository
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentContext
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentRequest
import com.github.mobdev778.aiadventchallenge.domain.chat.ObserveWindowMessagesUseCase
import com.github.mobdev778.aiadventchallenge.domain.invariant.InvariantRegistry
import com.github.mobdev778.aiadventchallenge.domain.mcpserver.McpServerInteractor
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Single

/**
 * Построитель контекста агента ([AgentContext]) на основе входящего [AgentRequest].
 *
 * Отвечает за агрегацию всех данных, необходимых агенту для выполнения шага:
 * активного профиля пользователя, текущего контекста задачи (если он есть),
 * актуального списка сообщений окна, набора инвариантов безопасности и
 * доступных инструментов MCP-серверов. Каждый вызов [build] создаёт
 * целостный снимок окружения, используя соответствующие репозитории и интеракторы.
 *
 * Компонент зарегистрирован как синглтон в Koin-контейнере (аннотация [@Single]).
 */
@Single
class AgentContextBuilder(
    private val profileRepository: ProfileRepository,
    private val taskContextRepository: TaskContextRepository,
    private val observeWindowMessagesUseCase: ObserveWindowMessagesUseCase,
    private val invariantRegistry: InvariantRegistry,
    private val mcpServerInteractor: McpServerInteractor,
) {

    /**
     * Строит полный [AgentContext] на основе запроса агента.
     *
     * Процесс сборки:
     * 1. Получает выбранный профиль через [ProfileRepository.getSelectedProfile].
     * 2. Если в запросе передан идентификатор контекста задачи ([AgentRequest.taskContextId]),
     *    загружает соответствующий [TaskContext] из [TaskContextRepository].
     * 3. Запрашивает текущий список сообщений окна через [ObserveWindowMessagesUseCase] и
     *    берёт первое значение из потока.
     * 4. Извлекает все зарегистрированные инварианты из [InvariantRegistry].
     * 5. Получает актуальный список активных инструментов MCP-серверов через
     *    [McpServerInteractor.activeToolsFlow] (первое значение потока).
     *
     * Полученные данные объединяются в неизменяемый объект [AgentContext].
     *
     * @param request Запрос, содержащий идентификаторы чата, задачи, родительского сообщения,
     *                текст запроса и метаданные.
     * @return Сформированный контекст агента, готовый к использованию в процессе выполнения шага.
     */
    suspend fun build(request: AgentRequest): AgentContext {
        val profile = profileRepository.getSelectedProfile()
        val taskContext = when {
            request.taskContextId != null -> taskContextRepository.getTaskContext(request.taskContextId)
            else -> null
        }
        val windowMessages = observeWindowMessagesUseCase.invoke(request.chatId).first()
        return AgentContext(
            profile = profile,
            taskContext = taskContext,
            windowMessages = windowMessages,
            invariants = invariantRegistry.getInvariants(),
            tools = mcpServerInteractor.activeToolsFlow.first(),
        )
    }
}
