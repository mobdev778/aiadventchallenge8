package com.github.mobdev778.aiadventchallenge.domain.chat

import com.github.mobdev778.aiadventchallenge.data.chat.repository.ChatRepository
import com.github.mobdev778.aiadventchallenge.data.settings.repository.SettingsRepository
import com.github.mobdev778.aiadventchallenge.domain.chatclient.ChatClient
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Message
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Role
import com.github.mobdev778.aiadventchallenge.domain.chat.model.Chat
import com.github.mobdev778.aiadventchallenge.domain.chat.model.ChatMessage
import com.github.mobdev778.aiadventchallenge.domain.chat.model.MessageType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import org.koin.core.annotation.Single
import java.util.UUID

@Single
class ChatInteractor(
    private val chatClient: ChatClient,
    private val chatRepository: ChatRepository,
    private val observeWindowMessagesUseCase: ObserveWindowMessagesUseCase,
    private val settingsRepository: SettingsRepository,
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

    suspend fun updateMessage(message: ChatMessage) {
        chatRepository.add(message)
    }

    suspend fun sendMessage(message: ChatMessage) {
        val requestMessages = windowMessages
            .map { message ->
                when (message.type) {
                    MessageType.User -> {
                        Message(
                            role = Role.User,
                            content = message.text
                        )
                    }
                    MessageType.Bot -> {
                        Message(
                            role = Role.Assistant,
                            content = message.text
                        )
                    }
                    MessageType.StickyFacts -> Message(
                        role = Role.System,
                        content = "[CRITICAL_STICKY_FACTS]\nИспользуй следующие неизменяемые пары ключ-значение для контекста. " +
                                "Ты обязан строго следовать этим данным и не имеешь права их выдумывать или менять:" +
                            message.text
                    )
                }
            }

        val baseModel = settingsRepository.getSettings().baseModel
            .trim()
            .replace("\n", "")

        sentMessages.value = message

        runCatching {
            chatClient.execute(
                ChatRequest(
                    model = baseModel,
                    messages = requestMessages + Message(role = Role.User, content = message.text),
                ),
            )
        }.onSuccess { response ->
            val promptTokens = response.usage?.promptTokens ?: 0

            val responseTokens = response.usage?.completionTokens ?: 0
            val answer = response.choices.firstOrNull()?.message?.content
                ?.takeIf { it.isNotBlank() }
                ?: "- no response -"
            chatRepository.add(
                listOf(
                    message.copy(
                        tokens = promptTokens,
                        parentId = windowMessages.lastOrNull()?.id
                    ),
                    ChatMessage(
                        id = UUID.randomUUID(),
                        chatId = message.chatId,
                        parentId = message.id,
                        branchB = false,
                        time = System.currentTimeMillis(),
                        text = answer,
                        type = MessageType.Bot,
                        tokens = responseTokens,
                        rank = 0,
                    )
                )
            )
            sentMessages.value = null
        }.onFailure { t ->
            chatRepository.add(
                listOf(
                    message.copy(
                        parentId = windowMessages.lastOrNull()?.id
                    ),
                    ChatMessage(
                        id = UUID.randomUUID(),
                        chatId = message.chatId,
                        parentId = message.id,
                        branchB = false,
                        time = System.currentTimeMillis(),
                        text = "Ошибка: ${t.message ?: t::class.java.simpleName}",
                        type = MessageType.Bot,
                        tokens = 0,
                        rank = 0,
                    )
                )
            )
            sentMessages.value = null
        }
    }

    suspend fun deleteAllMessages(chatId: UUID) {
        chatRepository.clearMessages(chatId)
    }
}