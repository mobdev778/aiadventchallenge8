package com.github.mobdev778.aiadventchallenge.presentation.chatscreen

import com.github.mobdev778.aiadventchallenge.data.chathistory.repository.ChatHistoryRepository
import com.github.mobdev778.aiadventchallenge.domain.chathistory.ChatAuthor
import com.github.mobdev778.aiadventchallenge.domain.chathistory.ChatMessage
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.ChatClient
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.model.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.model.Message
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.model.Role
import com.github.mobdev778.aiadventchallenge.domain.profile.AppProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatScreenStateHolder(
    private val chatClient: ChatClient,
    private val chatHistoryRepository: ChatHistoryRepository,
    private val appProfile: AppProfile,
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val inputTextFlow = MutableStateFlow("")
 
    val uiState: StateFlow<ChatScreenState> = combine(
        chatHistoryRepository.observe(),
        inputTextFlow,
    ) { messages: List<ChatMessage>, input: String ->
        ChatScreenState(messages, input)
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ChatScreenState(emptyList(), "")
    )

    fun onEvent(event: ChatScreenEvent) {
        when (event) {
            is ChatScreenEvent.OnInputTextChanged -> changeText(text = event.text)
            is ChatScreenEvent.OnSendMessageClick -> sendMessage()
            is ChatScreenEvent.OnClearAllMessagesClick -> clearAllMessages()
        }
    }

    private fun changeText(text: String) {
        inputTextFlow.update { text }
    }

    private fun sendMessage() {
        scope.launch {
            val text = uiState.value.inputText.trim()
            if (text.isEmpty()) return@launch

            inputTextFlow.update { "" }
            val now = System.currentTimeMillis()
            val newMessage = ChatMessage(
                id = now,
                text = text,
                author = ChatAuthor.User
            )

            val requestMessages = (uiState.value.messages + newMessage)
                .mapNotNull { uiMessage ->
                    when (uiMessage.author) {
                        ChatAuthor.User -> Message(role = Role.User, content = uiMessage.text)
                        ChatAuthor.Bot -> Message(role = Role.Assistant, content = uiMessage.text)
                    }
                }

            chatHistoryRepository.addMessage(newMessage)

            runCatching {
                chatClient.execute(
                    ChatRequest(
                        model = appProfile.baseModel,
                        messages = requestMessages,
                    ),
                )
            }.onSuccess { response ->
                val answer = response.choices.firstOrNull()?.message?.content
                    ?.takeIf { it.isNotBlank() }
                    ?: "- no response -"

                chatHistoryRepository.addMessage(
                    ChatMessage(
                        id = now + 1,
                        text = answer,
                        author = ChatAuthor.Bot,
                    )
                )
            }.onFailure { t ->
                chatHistoryRepository.addMessage(
                    ChatMessage(
                        id = now + 1,
                        text = "Ошибка: ${t.message ?: t::class.java.simpleName}",
                        author = ChatAuthor.Bot,
                    )
                )
            }
        }
    }

    private fun clearAllMessages() {
        scope.launch {
            chatHistoryRepository.clear()
        }
    }
}
