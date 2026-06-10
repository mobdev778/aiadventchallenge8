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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import org.koin.core.annotation.Single

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

    fun observeMessages(): Flow<List<ChatMessage>> = messages

    fun observeWindowMessages(): Flow<List<ChatMessage>> = windowMessages

    suspend fun sendMessage(message: ChatMessage) {
        chatHistoryRepository.add(message)

        val requestMessages = windowMessages.value
            .map { message ->
                when (message.author) {
                    ChatAuthor.User -> Message(role = Role.User, content = message.text)
                    ChatAuthor.Bot -> Message(role = Role.Assistant, content = message.text)
                }
            }

        val baseModel = settingsRepository.getSettings().baseModel

        runCatching {
            chatClient.execute(
                ChatRequest(
                    model = baseModel,
                    messages = requestMessages,
                ),
            )
        }.onSuccess { response ->
            val promptTokens = response.usage?.promptTokens ?: 0
            if (promptTokens > 0) {
                chatHistoryRepository.delete(message)
                chatHistoryRepository.add(
                    message.copy(tokens = promptTokens)
                )
            }

            val responseTokens = response.usage?.completionTokens ?: 0
            val answer = response.choices.firstOrNull()?.message?.content
                ?.takeIf { it.isNotBlank() }
                ?: "- no response -"
            chatHistoryRepository.add(
                ChatMessage(
                    id = message.id + 1,
                    text = answer,
                    author = ChatAuthor.Bot,
                    tokens = responseTokens
                )
            )
        }.onFailure { t ->
            chatHistoryRepository.add(
                ChatMessage(
                    id = message.id + 1,
                    text = "Ошибка: ${t.message ?: t::class.java.simpleName}",
                    author = ChatAuthor.Bot,
                    tokens = 0,
                )
            )
        }
    }

    suspend fun deleteAllMessages() {
        chatHistoryRepository.clear()
    }
}