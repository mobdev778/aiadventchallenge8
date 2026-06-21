package com.github.mobdev778.aiadventchallenge.presentation.chatscreen

import com.github.mobdev778.aiadventchallenge.data.taskcontext.repository.TaskContextRepository
import com.github.mobdev778.aiadventchallenge.domain.chat.ChatInteractor
import com.github.mobdev778.aiadventchallenge.domain.chat.model.Chat
import com.github.mobdev778.aiadventchallenge.domain.chat.model.ChatMessage
import com.github.mobdev778.aiadventchallenge.domain.chat.model.MessageType
import com.github.mobdev778.aiadventchallenge.domain.settings.SettingsInteractor
import com.github.mobdev778.aiadventchallenge.domain.settings.model.ContextManagementType
import com.github.mobdev778.aiadventchallenge.domain.task.TaskContext
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model.ChatScreenState
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model.ChatUiMessage
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model.ContextManagementState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
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
    private val taskContextRepository: TaskContextRepository,
    private val scope: CoroutineScope,
) {
    private val inputTextFlow = MutableStateFlow("")
    private val expandedMessagesFlow = MutableStateFlow<Set<UUID>>(emptySet())

    private val selectedChatIdFlow = MutableStateFlow<UUID?>(null)

    val commands = MutableSharedFlow<ChatScreenCommand>(
        // Так команда дождется, пока Compose-экран будет готов ее принять.
        extraBufferCapacity = 1
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val intermediateStateFlow: Flow<IntermediateState> = selectedChatIdFlow
        .flatMapLatest { chatId ->
            val chatId = chatId ?: UUID(0, 0)
            chatInteractor.observeChat(chatId).map { chat ->
                chat ?: Chat(
                    id = UUID(0, 0),
                    name = "",
                    time = 0L,
                    parentId = null,
                    taskContextId = null,
                )
            }.flatMapLatest { chat ->
                combine(
                    chatInteractor.observeMessages(chat.id),
                    chatInteractor.observeWindowMessages(chat.id),
                    chatInteractor.observeSentMessages(),
                    chatInteractor.observeTaskContext(chat.id),
                    chatInteractor.observeAutoPlay(chat.id),
                ) { messages, windowMessages, sentMessage, taskContext, autoPlay ->
                    IntermediateState(chat, messages, windowMessages, sentMessage, taskContext, autoPlay)
                }
            }
        }

    val uiState: StateFlow<ChatScreenState> = combine(
        intermediateStateFlow,
        inputTextFlow,
        settingsInteractor.observeSettings().map {
            StrategyState(
                it.contextManagementType,
                it.maxMessages,
                it.maxTokens,
                it.recursiveSummationMaxMessages,
                it.stickyFactsMaxMessages,
            )
        },
        expandedMessagesFlow,
    ) { intermediateState, input, strategyState, expandedIds ->
        val chat = intermediateState.chat

        // TODO подумать над более быстрым способом восстановления дерева через Room
        val windowIds = intermediateState.windowMessages.map { it.id }.toSet()

        val idMessageMap = intermediateState.messages
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
        for (message in intermediateState.messages) {
            if (message.parentId != null) {
                val children = idChildrenMap.getOrPut(message.parentId) { ArrayList() }
                val child = idMessageMap[message.id]
                if (child != null) { children.add(child) }
            }
        }

        var windowMessages = intermediateState.messages
            .filter { windowIds.contains(it.id) }
            .mapNotNull {
                val children = idChildrenMap.getOrDefault(it.id, ArrayList())
                idMessageMap[it.id]?.copy(
                    children = children
                )
            }

        windowMessages = if (intermediateState.sentMessage != null) {
            windowMessages + ChatUiMessage(
                rank = 0,
                message = intermediateState.sentMessage,
                insideWindow = true,
                children = emptyList(),
                expanded = false
            )
        } else windowMessages

        ChatScreenState(
            chat = chat,
            messages = windowMessages,
            contextManagementState = mapTokenLimitState(strategyState, windowMessages),
            inputText = input,
            taskContext = intermediateState.taskContext,
            autoPlay = intermediateState.autoPlay,
        )
    }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ChatScreenState(
                chat = Chat(
                    id = UUID(0, 0),
                    name = "",
                    time = 0L,
                    parentId = null,
                    taskContextId = null,
                ),
                messages = emptyList(),
                inputText = "",
                contextManagementState = ContextManagementState.None,
                taskContext = null,
                autoPlay = false,
            )
        )

    fun setChat(chatId: UUID) {
        selectedChatIdFlow.value = chatId
    }

    fun onEvent(event: ChatScreenEvent) {
        when (event) {
            is ChatScreenEvent.OnBackClick -> {
                commands.tryEmit(ChatScreenCommand.Back)
            }

            is ChatScreenEvent.OnInputTextChanged -> changeText(text = event.text)
            is ChatScreenEvent.OnMessageClicked -> expandCollapseMessage(event.message)
            is ChatScreenEvent.OnSendMessageClick -> sendMessage(inputTextFlow.value)
            is ChatScreenEvent.OnClearAllMessagesClick -> clearAllMessages()
            is ChatScreenEvent.OnStopAutoPlayClick -> stopAutoPlay()
            is ChatScreenEvent.OnContinueDialogClick -> sendMessage("Продолжай")
            is ChatScreenEvent.OnMessageBranchToggle -> toggleBotMessageBranch(event.message.message)

            is ChatScreenEvent.OnTaskStateIndicatorClick -> {
                val chatId = selectedChatIdFlow.value ?: return
                val taskContextId = uiState.value.taskContext?.id ?: return
                commands.tryEmit(ChatScreenCommand.OpenTaskContext(taskContextId = taskContextId, chatId = chatId))
            }
        }
    }

    private fun changeText(text: String) {
        inputTextFlow.update { text }
    }

    private fun stopAutoPlay() {
        scope.launch {
            val chatId = uiState.value.chat.id
            chatInteractor.stopAutoPlay(chatId)
        }
    }

    private fun sendMessage(text: String) {
        scope.launch(Dispatchers.Default) {
            val chatId = selectedChatIdFlow.value ?: return@launch
            if (text.isEmpty()) return@launch

            inputTextFlow.update { "" }

            chatInteractor.sendMessage(
                chat = uiState.value.chat,
                parentMessageId = uiState.value.messages.lastOrNull()?.message?.id,
                message = ChatMessage(
                    id = UUID.randomUUID(),
                    chatId = chatId,
                    parentId = null,
                    time = System.currentTimeMillis(),
                    branchB = false,
                    text = text,
                    type = MessageType.User,
                    tokens = 0, // мы не знаем в начале предполагаемый размер сообщения
                    rank = 0,
                )
            )
        }
    }

    private fun clearAllMessages() {
        scope.launch {
            val uiState = uiState.value
            chatInteractor.deleteAllMessages(uiState.chat.id)
            val taskContext = uiState.taskContext
            if (taskContext != null) {
                taskContextRepository.deleteTaskContext(taskContext.id)
            }
        }
    }

    private fun expandCollapseMessage(message: ChatUiMessage) {
        scope.launch {
            expandedMessagesFlow.update { ids ->
                if (message.expanded) { ids - message.message.id } else { ids + message.message.id }
            }
        }
    }

    private fun toggleBotMessageBranch(message: ChatMessage) {
        scope.launch {
            chatInteractor.updateMessage(message.copy(branchB = !message.branchB))
        }
    }

    private fun mapTokenLimitState(strategyState: StrategyState, windowMessages: List<ChatUiMessage>): ContextManagementState {
        return when (strategyState.contextManagementType) {
            ContextManagementType.None -> {
                ContextManagementState.None
            }

            ContextManagementType.SlidingWindow -> {
                ContextManagementState.SlidingWindow(windowMessages.size, strategyState.maxMessages)
            }

            ContextManagementType.StickyFacts -> {
                ContextManagementState.StickFacts(
                    windowMessages.filter {
                        it.message.type == MessageType.User || it.message.type == MessageType.Bot
                    }.size,
                    strategyState.recursiveSummationMaxMessages,
                )
            }

            ContextManagementType.Branching -> {
                ContextManagementState.Branching
            }
        }
    }

    private data class StrategyState(
        val contextManagementType: ContextManagementType,
        val maxMessages: Int,
        val maxTokens: Int,
        val recursiveSummationMaxMessages: Int,
        val stickyFactsMaxMessages: Int,
    )

    private data class IntermediateState(
        val chat: Chat,
        val messages: List<ChatMessage>,
        val windowMessages: List<ChatMessage>,
        val sentMessage: ChatMessage?,
        val taskContext: TaskContext?,
        val autoPlay: Boolean,
    )
}
