package com.github.mobdev778.aiadventchallenge.presentation.chatscreen

import com.github.mobdev778.aiadventchallenge.domain.chathistory.ChatInteractor
import com.github.mobdev778.aiadventchallenge.domain.chathistory.model.ChatAuthor
import com.github.mobdev778.aiadventchallenge.domain.chathistory.model.ChatMessage
import com.github.mobdev778.aiadventchallenge.domain.messageselection.MessageSelectionType
import com.github.mobdev778.aiadventchallenge.domain.settings.SettingsInteractor
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model.ChatScreenState
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model.ChatUiMessage
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model.TokenLimitState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Singleton

@Singleton
class ChatScreenStateHolder(
    private val chatInteractor: ChatInteractor,
    private val settingsInteractor: SettingsInteractor,
    private val scope: CoroutineScope,
) {

    private val inputTextFlow = MutableStateFlow("")

    val uiState: StateFlow<ChatScreenState> = combine(
        chatInteractor.observeMessages(),
        chatInteractor.observeWindowMessages(),
        inputTextFlow,
        settingsInteractor.observeSettings().map {
            StrategyState(it.messageSelectionType, it.maxMessages, it.maxTokens)
        },
    ) { messages: List<ChatMessage>, windowMessages: List<ChatMessage>, input: String, strategyState: StrategyState ->
        val windowSet = windowMessages.toSet()
        ChatScreenState(
            messages = messages.map { ChatUiMessage(it, windowSet.contains(it)) },
            tokenLimitState = mapTokenLimitState(strategyState, windowMessages),
            inputText = input,
        )
    }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ChatScreenState(
                messages = emptyList(),
                inputText = "",
                tokenLimitState = TokenLimitState.FullHistory,
            )
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
            val text = inputTextFlow.value
            if (text.isEmpty()) return@launch

            inputTextFlow.update { "" }

            chatInteractor.sendMessage(
                ChatMessage(
                    id = System.currentTimeMillis(),
                    text = text,
                    author = ChatAuthor.User,
                    tokens = 0, // мы не знаем в начале предполагаемый размер сообщения
                )
            )
        }
    }

    private fun clearAllMessages() {
        scope.launch {
            chatInteractor.deleteAllMessages()
        }
    }

    private fun mapTokenLimitState(strategyState: StrategyState, windowMessages: List<ChatMessage>): TokenLimitState {
        return when (strategyState.messageSelectionType) {
            MessageSelectionType.FullHistory -> TokenLimitState.FullHistory
            MessageSelectionType.MessageLimit -> {
                TokenLimitState.LimitMessages(windowMessages.size, strategyState.maxMessages)
            }

            MessageSelectionType.TokenLimit -> {
                TokenLimitState.LimitTokens(windowMessages.sumOf { it.tokens }, strategyState.maxTokens)
            }
        }
    }

    private data class StrategyState(
        val messageSelectionType: MessageSelectionType,
        val maxMessages: Int,
        val maxTokens: Int,
    )
}
