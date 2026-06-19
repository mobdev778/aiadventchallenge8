package com.github.mobdev778.aiadventchallenge.domain.chat

import com.github.mobdev778.aiadventchallenge.data.chat.repository.ChatRepository
import com.github.mobdev778.aiadventchallenge.data.taskcontext.repository.TaskContextRepository
import com.github.mobdev778.aiadventchallenge.domain.chat.model.Chat
import com.github.mobdev778.aiadventchallenge.domain.chat.model.ChatMessage
import com.github.mobdev778.aiadventchallenge.domain.chat.model.MessageType
import com.github.mobdev778.aiadventchallenge.domain.task.TaskContext
import com.github.mobdev778.aiadventchallenge.domain.task.TaskState
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
import org.koin.core.annotation.Single
import java.util.UUID

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@Single
class ChatInteractor(
    private val chatRepository: ChatRepository,
    private val observeWindowMessagesUseCase: ObserveWindowMessagesUseCase,
    private val chatOrchestrator: ChatOrchestrator,
    private val taskContextRepository: TaskContextRepository,
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

    suspend fun sendMessage(context: ChatContext, message: ChatMessage) {
        startAutoPlay(context.chatId)

        var message = message
        var context = context
        var botResponse: ChatMessage
        do {
            sentMessages.value = message
            try {
                val (response, promptTokens) = chatOrchestrator.sendMessage(context, message)
                chatRepository.add(
                    listOf(
                        message.copy(
                            tokens = promptTokens,
                            parentId = windowMessages.lastOrNull()?.id
                        ),
                        response.copy(
                            parentId = message.id,
                        )
                    )
                )
                botResponse = response

                val chat = chatRepository.observeChat(message.chatId).first()!!
                context = context.copy(
                    taskContext = chat.taskContextId?.let { taskContextRepository.getTaskContext(it) }
                )

                message = ChatMessage(
                    id = UUID.randomUUID(),
                    chatId = message.chatId,
                    parentId = null,
                    time = System.currentTimeMillis(),
                    branchB = false,
                    text = when {
                        context.taskContext?.state == TaskState.PrintResult -> "Продолжай. Выведи финальный результат решения"
                        else -> "Продолжай"
                    },
                    type = MessageType.User,
                    tokens = 0, // мы не знаем в начале предполагаемый размер сообщения
                    rank = 0,
                )
            } finally {
                sentMessages.value = null
            }
        } while (
            botResponse.text.contains("[next_step]") &&
            context.taskContext?.state != TaskState.Done &&
            autoPlayChatIdsFlow.value.contains(context.chatId)
        )
        stopAutoPlay(context.chatId)
    }

    suspend fun deleteAllMessages(chatId: UUID) {
        chatRepository.clearMessages(chatId)
        taskContextRepository.clearTaskContext()
        stopAutoPlay(chatId)
    }
}
