package com.github.mobdev778.aiadventchallenge.presentation.chatscreen

import com.github.mobdev778.aiadventchallenge.domain.chathistory.ChatInteractor
import com.github.mobdev778.aiadventchallenge.domain.chathistory.model.ChatAuthor
import com.github.mobdev778.aiadventchallenge.domain.chathistory.model.ChatMessage
import com.github.mobdev778.aiadventchallenge.domain.settings.SettingsInteractor
import com.github.mobdev778.aiadventchallenge.domain.settings.model.MessageSelectionType
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model.ChatScreenState
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model.ChatUiMessage
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model.TokenLimitState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Singleton
import java.util.UUID

@Singleton
class ChatScreenStateHolder(
    private val chatInteractor: ChatInteractor,
    private val settingsInteractor: SettingsInteractor,
    private val scope: CoroutineScope,
) {
    private val inputTextFlow = MutableStateFlow("")
    private val expandedMessagesFlow = MutableStateFlow<Set<UUID>>(emptySet())

    val uiState: StateFlow<ChatScreenState> = combine(
        combine(
            chatInteractor.observeMessages(),
            chatInteractor.observeWindowMessages(),
            chatInteractor.observeSentMessages(),
        ) { messages, windowMessages, sentMessage ->
            ChatInteractorData(messages, windowMessages, sentMessage)
        }.distinctUntilChanged(),
        inputTextFlow,
        settingsInteractor.observeSettings().map {
            StrategyState(
                it.messageSelectionType,
                it.maxMessages,
                it.maxTokens,
                it.recursiveSummationMaxMessages
            )
        },
        expandedMessagesFlow,
    ) { chatData, input, strategyState, expandedIds ->

        // TODO подумать над более быстрым способом восстановления дерева через Room
        val windowIds = chatData.windowMessages.map { it.id }.toSet()

        val idMessageMap = chatData.messages
            .map {
                it.id to ChatUiMessage(
                    rank = it.rank,
                    message = it,
                    insideWindow = windowIds.contains(it.id),
                    children = emptyList(),
                    expanded = expandedIds.contains(it.id)
                )
            }
            .toMap()
            .toMutableMap()

        val idChildrenMap = HashMap<UUID, MutableList<ChatUiMessage>>()
        for (message in chatData.messages) {
            if (message.parentId != null) {
                val children = idChildrenMap.getOrPut(message.parentId) { ArrayList() }
                val child = idMessageMap[message.id]
                if (child != null) { children.add(child) }
            }
        }

        var windowMessages = chatData.messages
            .filter { it.parentId == null } // возвращаем только корневые элементы
            .filter { windowIds.contains(it.id) }
            .mapNotNull {
                val children = idChildrenMap.getOrDefault(it.id, ArrayList())
                idMessageMap[it.id]?.copy(
                    children = children
                )
            }

        windowMessages = if (chatData.sentMesssage != null) {
            windowMessages + ChatUiMessage(
                rank = 0,
                message = chatData.sentMesssage,
                insideWindow = true,
                children = emptyList(),
                expanded = false
            )
        } else windowMessages

        ChatScreenState(
            messages = windowMessages,
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
            is ChatScreenEvent.OnMessageClicked -> expandCollapseMessage(event.message)
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
                    id = UUID.randomUUID(),
                    parentId = null,
                    time = System.currentTimeMillis(),
                    text = text,
                    author = ChatAuthor.User,
                    tokens = 0, // мы не знаем в начале предполагаемый размер сообщения
                    rank = 0,
                )
            )
        }
    }

    private fun clearAllMessages() {
        scope.launch {
            chatInteractor.deleteAllMessages()
        }
    }

    private fun expandCollapseMessage(message: ChatUiMessage) {
        scope.launch {
            expandedMessagesFlow.update { ids ->
                if (message.expanded) { ids - message.message.id } else { ids + message.message.id }
            }
        }
    }

    private fun mapTokenLimitState(strategyState: StrategyState, windowMessages: List<ChatUiMessage>): TokenLimitState {
        return when (strategyState.messageSelectionType) {
            MessageSelectionType.FullHistory -> {
                TokenLimitState.FullHistory
            }

            MessageSelectionType.MessageLimit -> {
                TokenLimitState.LimitMessages(windowMessages.size, strategyState.maxMessages)
            }

            MessageSelectionType.TokenLimit -> {
                TokenLimitState.LimitTokens(windowMessages.sumOf { it.message.tokens }, strategyState.maxTokens)
            }

            MessageSelectionType.RecursiveSummation -> {
                TokenLimitState.RecursiveSummation(
                    windowMessages.size,
                    strategyState.recursiveSummationMaxMessages,
                )
            }
        }
    }

    private data class StrategyState(
        val messageSelectionType: MessageSelectionType,
        val maxMessages: Int,
        val maxTokens: Int,
        val recursiveSummationMaxMessages: Int,
    )

    private data class ChatInteractorData(
        val messages: List<ChatMessage>,
        val windowMessages: List<ChatMessage>,
        val sentMesssage: ChatMessage?,
    )
}
