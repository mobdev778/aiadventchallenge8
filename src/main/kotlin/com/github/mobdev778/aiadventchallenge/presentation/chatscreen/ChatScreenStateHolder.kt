package com.github.mobdev778.aiadventchallenge.presentation.chatscreen

import com.github.mobdev778.aiadventchallenge.domain.openai.chat.ChatClient
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.model.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.model.Message
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.model.Role
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatScreenStateHolder(
    private val chatClient: ChatClient,
    private val model: String = "gpt-4.1",
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _uiState = MutableStateFlow(ChatScreenUiState())
    val uiState = _uiState.asStateFlow()

    fun onInputTextChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun onSendClicked() {
        val text = uiState.value.inputText.trim()
        if (text.isEmpty()) return

        val now = System.currentTimeMillis()

        // 1) Optimistically add user message and clear input
        _uiState.update { current ->
            current.copy(
                inputText = "",
                messages = current.messages + ChatMessageUi(
                    id = now,
                    text = text,
                    author = ChatAuthor.User,
                ),
            )
        }

        // 2) Ask LLM in background and append assistant message
        // Build request from the whole chat history (including the message we just appended).
        val requestMessages = uiState.value.messages
            .mapNotNull { uiMessage ->
                when (uiMessage.author) {
                    ChatAuthor.User -> Message(role = Role.User, content = uiMessage.text)
                    ChatAuthor.Bot -> Message(role = Role.Assistant, content = uiMessage.text)
                }
            }

        scope.launch {
            runCatching {
                chatClient.execute(
                    ChatRequest(
                        model = model,
                        messages = requestMessages,
                    ),
                )
            }.onSuccess { response ->
                val answer = response.choices.firstOrNull()?.message?.content
                    ?.takeIf { it.isNotBlank() }
                    ?: "- no response -"

                _uiState.update { current ->
                    current.copy(
                        messages = current.messages + ChatMessageUi(
                            id = now + 1,
                            text = answer,
                            author = ChatAuthor.Bot,
                        ),
                    )
                }
            }.onFailure { t ->
                _uiState.update { current ->
                    current.copy(
                        messages = current.messages + ChatMessageUi(
                            id = now + 1,
                            text = "Ошибка: ${t.message ?: t::class.java.simpleName}",
                            author = ChatAuthor.Bot,
                        ),
                    )
                }
            }
        }
    }
}
