package com.github.mobdev778.aiadventchallenge.domain.chat

import com.github.mobdev778.aiadventchallenge.data.chat.repository.ChatRepository
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

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@Single
class ChatInteractor(
    private val chatRepository: ChatRepository,
    private val observeWindowMessagesUseCase: ObserveWindowMessagesUseCase,
    private val taskContextRepository: TaskContextRepository,
    private val agentOrchestrator: AgentOrchestrator,
    private val scope: CoroutineScope,
) {

    private val sentMessages = MutableStateFlow<ChatMessage?>(null)
    private val autoPlayChatIdsFlow = MutableStateFlow<Set<UUID>>(emptySet())

    private var windowMessages: List<ChatMessage> = emptyList()

    fun observeChat(chatId: UUID): Flow<Chat?> =
        chatRepository.observeChat(chatId)
            .flowOn(Dispatchers.IO)

    fun observeMessages(chatId: UUID): Flow<List<ChatMessage>> =
        chatRepository.observeMessages(chatId)
            .flowOn(Dispatchers.IO)

    fun observeWindowMessages(chatId: UUID): Flow<List<ChatMessage>> =
        observeWindowMessagesUseCase.invoke(chatId)
            .flowOn(Dispatchers.Default)
            .onEach { windowMessages = it }

    fun observeSentMessages(): Flow<ChatMessage?> = sentMessages

    fun observeAutoPlay(chatId: UUID): Flow<Boolean> = autoPlayChatIdsFlow
        .map { ids -> ids.contains(chatId) }
        .distinctUntilChanged()

    fun observeTaskContext(chatId: UUID): Flow<TaskContext?> =
        chatRepository.observeChat(chatId)
            .flatMapLatest { chat ->
                if (chat?.taskContextId == null) {
                    flowOf(null)
                } else {
                    taskContextRepository.observeTaskContext(chat.taskContextId)
                }
            }

    suspend fun updateMessage(message: ChatMessage) {
        chatRepository.add(message)
    }

    suspend fun isAutoPlayEnabled(chatId: UUID): Boolean {
        return autoPlayChatIdsFlow.value.contains(chatId)
    }

    suspend fun stopAutoPlay(chatId: UUID) {
        autoPlayChatIdsFlow.update { ids ->
            ids - chatId
        }
    }

    private fun startAutoPlay(chatId: UUID) {
        autoPlayChatIdsFlow.update { ids ->
            ids + chatId
        }
    }

    init {
        scope.launch(Dispatchers.Default) {
            agentOrchestrator.responses.collect { response ->
                handleAgentResponse(response)
            }
        }
    }

    // отправляем агенту сообщение
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

    // принимаем ответ от агента
    private suspend fun handleAgentResponse(response: AgentResponse) {
        sentMessages.value = null

        val chat = chatRepository.observeChat(response.request.chatId).first()!!

        if (response.taskContext != null) {
            taskContextRepository.saveTaskContext(response.taskContext)
            chatRepository.add(chat.copy(taskContextId = response.taskContext.id))
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
            type = MessageType.User,
            tokens = response.requestTokens,
            rank = 0,
        )
        chatRepository.add(listOf(userMessage, botMessage))

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

    private fun containsAutoPlayMessage(text: String): Boolean {
        for (autoPlayMessage in autoPlayMessages) {
            if (text.contains(autoPlayMessage)) {
                return true
            }
        }
        return false
    }

    suspend fun deleteAllMessages(chatId: UUID) {
        chatRepository.clearMessages(chatId)
        taskContextRepository.clearTaskContext()
        stopAutoPlay(chatId)
    }

    val autoPlayMessages = listOf(
        "[next_step]", "[Нарушение]: ", "[EXECUTION]", "[VALIDATION]", "[SUMMARIZE]", "[PLANNING]"
    )
}
