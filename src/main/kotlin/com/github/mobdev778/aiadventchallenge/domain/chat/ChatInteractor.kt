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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import org.koin.core.annotation.Single
import java.util.UUID

@Single
class ChatInteractor(
    private val chatRepository: ChatRepository,
    private val observeWindowMessagesUseCase: ObserveWindowMessagesUseCase,
    private val chatOrchestrator: ChatOrchestrator,
    private val taskContextRepository: TaskContextRepository,
) {

    private val sentMessages = MutableStateFlow<ChatMessage?>(null)
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

    suspend fun sendMessage(context: ChatContext, message: ChatMessage) {
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
                message = ChatMessage(
                    id = UUID.randomUUID(),
                    chatId = message.chatId,
                    parentId = null,
                    time = System.currentTimeMillis(),
                    branchB = false,
                    text = "Продолжай",
                    type = MessageType.User,
                    tokens = 0, // мы не знаем в начале предполагаемый размер сообщения
                    rank = 0,
                )
                val chat = chatRepository.observeChat(message.chatId).first()!!
                context = context.copy(
                    taskContext = chat.taskContextId?.let { taskContextRepository.getTaskContext(it) }
                )
            } finally {
                sentMessages.value = null
            }
        } while (
            botResponse.text.endsWith("next_step") && context.taskContext?.state != TaskState.Done
        )
    }

    suspend fun deleteAllMessages(chatId: UUID) {
        chatRepository.clearMessages(chatId)
        taskContextRepository.clearTaskContext()
    }
}