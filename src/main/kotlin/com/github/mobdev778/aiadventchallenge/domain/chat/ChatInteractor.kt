package com.github.mobdev778.aiadventchallenge.domain.chat

import com.github.mobdev778.aiadventchallenge.data.rag.repository.RagChatRepository
import com.github.mobdev778.aiadventchallenge.data.taskcontext.repository.TaskContextRepository
import com.github.mobdev778.aiadventchallenge.domain.agent.AgentOrchestrator
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentRequest
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentResponse
import com.github.mobdev778.aiadventchallenge.domain.chat.model.Chat
import com.github.mobdev778.aiadventchallenge.domain.chat.model.ChatMessage
import com.github.mobdev778.aiadventchallenge.domain.chat.model.MessageType
import com.github.mobdev778.aiadventchallenge.domain.task.TaskContext
import com.github.mobdev778.aiadventchallenge.domain.task.TaskState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import java.util.UUID

/**
 * Центральный интерактор (use‑case) домена чата.
 *
 * Координирует операции чтения и записи сообщений, управление автоматическим
 * продолжением диалога (автоплэй), взаимодействие с агентами через
 * [AgentOrchestrator] и синхронизацию контекста задачи ([TaskContext]).
 *
 * @property ragChatRepository Репозиторий чатов с поддержкой RAG‑поиска.
 * @property observeWindowMessagesUseCase Use‑case для получения «окна» сообщений.
 * @property taskContextRepository Репозиторий для работы с контекстом задачи.
 * @property agentOrchestrator Оркестратор интеллектуальных агентов.
 * @property scope Корневая корутина для запуска асинхронных операций.
 */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@Single
@Suppress("TooManyFunctions")
class ChatInteractor(
    private val ragChatRepository: RagChatRepository,
    private val observeWindowMessagesUseCase: ObserveWindowMessagesUseCase,
    private val taskContextRepository: TaskContextRepository,
    private val agentOrchestrator: AgentOrchestrator,
    private val scope: CoroutineScope,
) {

    private val sentMessages = MutableStateFlow<ChatMessage?>(null)
    private val autoPlayChatIdsFlow = MutableStateFlow<Set<UUID>>(emptySet())

    private var windowMessages: List<ChatMessage> = emptyList()

    /**
     * Возвращает поток ([Flow]) объекта [Chat] по идентификатору чата.
     *
     * Подписчик будет получать актуальные данные чата при каждом изменении в репозитории.
     * Операции чтения выполняются на диспетчере [Dispatchers.IO].
     *
     * @param chatId Идентификатор чата.
     * @return Поток [Chat] или `null`, если чат не найден.
     */
    fun observeChat(chatId: UUID): Flow<Chat?> =
        ragChatRepository.observeChat(chatId)
            .flowOn(Dispatchers.IO)

    /**
     * Возвращает поток списка всех сообщений чата.
     *
     * При добавлении, изменении или удалении сообщений в репозитории подписчик
     * получает обновлённый список. Операции чтения выполняются на [Dispatchers.IO].
     *
     * @param chatId Идентификатор чата.
     * @return Поток списков [ChatMessage].
     */
    fun observeMessages(chatId: UUID): Flow<List<ChatMessage>> =
        ragChatRepository.observeMessages(chatId)
            .flowOn(Dispatchers.IO)

    /**
     * Возвращает поток «окна» сообщений — ограниченного набора последних сообщений,
     * определённого бизнес‑логикой [ObserveWindowMessagesUseCase].
     *
     * В отличие от [observeMessages], предоставляет только сообщения, укладывающиеся
     * в заданный лимит контекстного окна. Параллельно обновляет внутреннее поле
     * [windowMessages] для последующего использования в логике автоплэя.
     *
     * @param chatId Идентификатор чата.
     * @return Поток списков [ChatMessage], умещающихся в окно.
     */
    fun observeWindowMessages(chatId: UUID): Flow<List<ChatMessage>> =
        observeWindowMessagesUseCase.invoke(chatId)
            .flowOn(Dispatchers.Default)
            .onEach { windowMessages = it }

    /**
     * Возвращает поток последнего отправленного сообщения.
     *
     * Позволяет подписчикам реагировать на факт отправки пользователем нового сообщения.
     * Значение сбрасывается в `null` после завершения обработки агентом.
     *
     * @return Поток [ChatMessage] или `null`, если активной отправки нет.
     */
    fun observeSentMessages(): Flow<ChatMessage?> = sentMessages

    /**
     * Возвращает поток, сигнализирующий о состоянии автоматического продолжения диалога.
     *
     * Для указанного чата эмитирует `true`, если автоплэй активен, и `false` в противном случае.
     * Повторяющиеся значения отфильтровываются.
     *
     * @param chatId Идентификатор чата.
     * @return Поток [Boolean].
     */
    fun observeAutoPlay(chatId: UUID): Flow<Boolean> = autoPlayChatIdsFlow
        .map { ids -> ids.contains(chatId) }
        .distinctUntilChanged()

    /**
     * Возвращает поток контекста задачи, связанного с чатом.
     *
     * Если чат не имеет привязанного контекста задачи, эмитируется `null`.
     * При обновлении самого чата или его контекста подписчик получает актуальное значение.
     *
     * @param chatId Идентификатор чата.
     * @return Поток [TaskContext] или `null`.
     */
    fun observeTaskContext(chatId: UUID): Flow<TaskContext?> =
        ragChatRepository.observeChat(chatId)
            .flatMapLatest { chat ->
                if (chat?.taskContextId == null) {
                    flowOf(null)
                } else {
                    taskContextRepository.observeTaskContext(chat.taskContextId)
                }
            }

    /**
     * Сохраняет (вставляет или обновляет) сообщение в репозитории.
     *
     * @param message Сообщение для сохранения.
     */
    suspend fun updateMessage(message: ChatMessage) {
        ragChatRepository.add(message)
    }

    /**
     * Проверяет, включён ли режим автоплэя для заданного чата.
     *
     * @param chatId Идентификатор чата.
     * @return `true`, если автоплэй активен, иначе `false`.
     */
    suspend fun isAutoPlayEnabled(chatId: UUID): Boolean {
        return autoPlayChatIdsFlow.value.contains(chatId)
    }

    /**
     * Выключает автоматическое продолжение диалога для указанного чата.
     *
     * @param chatId Идентификатор чата.
     */
    suspend fun stopAutoPlay(chatId: UUID) {
        autoPlayChatIdsFlow.update { ids ->
            ids - chatId
        }
    }

    /**
     * Включает автоматическое продолжение диалога для указанного чата.
     *
     * @param chatId Идентификатор чата.
     */
    private fun startAutoPlay(chatId: UUID) {
        autoPlayChatIdsFlow.update { ids ->
            ids + chatId
        }
    }

    init {
        scope.launch(Dispatchers.Default) {
            agentOrchestrator.responses.collect { response ->
                if (!response.intermediate) {
                    handleAgentResponse(response)
                }
            }
        }
    }

    /**
     * Отправляет сообщение пользователя интеллектуальному агенту.
     *
     * Запускает агентов (если они ещё не запущены), включает автоплэй для данного чата,
     * формирует [AgentRequest] и направляет его оркестратору.
     *
     * @param chat Текущий объект чата, из которого берётся идентификатор контекста задачи.
     * @param parentMessageId Идентификатор родительского сообщения для ветвления диалога.
     * @param message Сообщение пользователя для обработки.
     */
    suspend fun sendMessage(chat: Chat, parentMessageId: UUID?, message: ChatMessage) {
        // запускаем агентов, если они еще не были запущены
        agentOrchestrator.startAgents()

        startAutoPlay(message.chatId)
        sentMessages.value = message

        val agentRequest = AgentRequest(
            chatId = message.chatId,
            taskContextId = chat.taskContextId,
            parentMessageId = parentMessageId,
            time = message.time,
            query = message.text,
        )
        // отправляем агенту запрос
        agentOrchestrator.asyncRequest(agentRequest)
    }

    /**
     * Обрабатывает финальный ответ агента, обновляет состояние чата и контекст задачи,
     * записывает сообщения пользователя и бота в репозиторий.
     *
     * Если в ответе агента обнаружена строка‑триггер автоплэя (из [autoPlayMessages]) и
     * контекст задачи не завершён, автоматически создаётся и отправляется контрольное
     * сообщение «Продолжай». В противном случае автоплэй выключается.
     *
     * @param response Финальный ответ агента.
     */
    private suspend fun handleAgentResponse(response: AgentResponse) {
        sentMessages.value = null

        val chat = ragChatRepository.observeChat(response.request.chatId).first()!!

        if (response.taskContext != null) {
            taskContextRepository.saveTaskContext(response.taskContext)
            ragChatRepository.add(chat.copy(taskContextId = response.taskContext.id))
        }

        val userMessage = ChatMessage(
            id = UUID.randomUUID(),
            chatId = chat.id,
            parentId = response.request.parentMessageId,
            time = response.request.time,
            branchB = false,
            text = response.request.query,
            type = MessageType.User,
            tokens = response.requestTokens,
            rank = 0,
        )
        val botMessage = ChatMessage(
            id = UUID.randomUUID(),
            chatId = chat.id,
            parentId = userMessage.id,
            time = System.currentTimeMillis(),
            branchB = false,
            text = response.message,
            type = MessageType.Bot,
            tokens = response.requestTokens,
            rank = 0,
        )
        ragChatRepository.add(listOf(userMessage, botMessage))

        val isAutoMessagePossible = containsAutoPlayMessage(response.message) &&
                response.taskContext?.state != TaskState.Done &&
                isAutoPlayEnabled(response.request.chatId)
        if (isAutoMessagePossible) {
            val autoMessage = ChatMessage(
                id = UUID.randomUUID(),
                chatId = chat.id,
                parentId = botMessage.id,
                time = System.currentTimeMillis(),
                branchB = false,
                text = "Продолжай",
                type = MessageType.User,
                tokens = 0, // мы не знаем в начале предполагаемый размер сообщения
                rank = 0,
            )
            scope.launch {
                sendMessage(chat, botMessage.id, autoMessage)
            }
        } else {
            stopAutoPlay(response.request.chatId)
        }
    }

    /**
     * Проверяет, содержит ли текст агента хотя бы один из триггеров автоплэя,
     * определённых в [autoPlayMessages].
     *
     * @param text Текст ответа агента.
     * @return `true`, если найден триггер, иначе `false`.
     */
    private fun containsAutoPlayMessage(text: String): Boolean {
        for (autoPlayMessage in autoPlayMessages) {
            if (text.contains(autoPlayMessage)) {
                return true
            }
        }
        return false
    }

    /**
     * Удаляет все сообщения чата и связанный контекст задачи, останавливает автоплэй.
     *
     * @param chatId Идентификатор чата.
     */
    suspend fun deleteAllMessages(chatId: UUID) {
        ragChatRepository.clearMessages(chatId)
        taskContextRepository.clearTaskContext()
        stopAutoPlay(chatId)
    }

    /**
     * Список строк-триггеров, присутствие которых в ответе агента запускает
     * автоматическое продолжение диалога (отправку контрольного сообщения «Продолжай»).
     *
     * Используется в [containsAutoPlayMessage].
     */
    val autoPlayMessages = listOf(
        "[next_step]", "[Нарушение]: ", "[EXECUTION]", "[VALIDATION]", "[SUMMARIZE]", "[PLANNING]"
    )
}
