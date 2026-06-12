package com.github.mobdev778.aiadventchallenge.domain.chathistory

import com.github.mobdev778.aiadventchallenge.data.chathistory.repository.ChatHistoryRepository
import com.github.mobdev778.aiadventchallenge.data.settings.repository.SettingsRepository
import com.github.mobdev778.aiadventchallenge.domain.chatclient.ChatClient
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Message
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Role
import com.github.mobdev778.aiadventchallenge.domain.chathistory.model.ChatAuthor
import com.github.mobdev778.aiadventchallenge.domain.chathistory.model.ChatMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import org.koin.core.annotation.Single
import java.util.UUID

@Single
class ChatInteractor(
    private val chatClient: ChatClient,
    private val chatHistoryRepository: ChatHistoryRepository,
    private val observeWindowMessagesUseCase: ObserveWindowMessagesUseCase,
    private val settingsRepository: SettingsRepository,
    private val scope: CoroutineScope,
) {

    private val messages = chatHistoryRepository
        .observe()
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val windowMessages = observeWindowMessagesUseCase.invoke()
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val sentMessages = MutableStateFlow<ChatMessage?>(null)

    fun observeMessages(): Flow<List<ChatMessage>> = messages

    fun observeWindowMessages(): Flow<List<ChatMessage>> = windowMessages

    fun observeSentMessages(): Flow<ChatMessage?> = sentMessages

    suspend fun sendMessage(message: ChatMessage) {
        val requestMessages = windowMessages.value
            .map { message ->
                when (message.author) {
                    ChatAuthor.User -> Message(role = Role.User, content = message.text)
                    ChatAuthor.Bot -> Message(role = Role.Assistant, content = message.text)
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
            sentMessages.value = null
            val promptTokens = response.usage?.promptTokens ?: 0

            val responseTokens = response.usage?.completionTokens ?: 0
            val answer = response.choices.firstOrNull()?.message?.content
                ?.takeIf { it.isNotBlank() }
                ?: "- no response -"
            chatHistoryRepository.add(
                listOf(
                    message.copy(
                        tokens = promptTokens
                    ),
                    ChatMessage(
                        id = UUID.randomUUID(),
                        parentId = null,
                        time = System.currentTimeMillis(),
                        text = answer,
                        author = ChatAuthor.Bot,
                        tokens = responseTokens,
                        rank = 0,
                    )
                )
            )
        }.onFailure { t ->
            sentMessages.value = null
            chatHistoryRepository.add(
                listOf(
                    message,
                    ChatMessage(
                        id = UUID.randomUUID(),
                        parentId = null,
                        time = System.currentTimeMillis(),
                        text = "Ошибка: ${t.message ?: t::class.java.simpleName}",
                        author = ChatAuthor.Bot,
                        tokens = 0,
                        rank = 0,
                    )
                )
            )
        }
    }

    suspend fun deleteAllMessages() {
        chatHistoryRepository.clear()
    }
}