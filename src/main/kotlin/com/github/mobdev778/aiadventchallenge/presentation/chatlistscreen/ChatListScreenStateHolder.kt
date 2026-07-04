package com.github.mobdev778.aiadventchallenge.presentation.chatlistscreen

import com.github.mobdev778.aiadventchallenge.data.chat.repository.ChatRepository
import com.github.mobdev778.aiadventchallenge.domain.chat.model.Chat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import java.util.UUID

private const val STATE_FLOW_TIMEOUT_MS = 5000L

@Single
class ChatListScreenStateHolder(
    private val chatRepository: ChatRepository,
    private val scope: CoroutineScope,
) {
    val chats: StateFlow<List<Chat>> = chatRepository
        .observeChats()
        .flowOn(Dispatchers.IO)
        .stateIn(scope, SharingStarted.WhileSubscribed(STATE_FLOW_TIMEOUT_MS), emptyList())

    val commands = MutableSharedFlow<ChatListScreenCommand>(
        // Так команда дождется, пока Compose-экран будет готов ее принять.
        extraBufferCapacity = 1
    )

    fun onEvent(event: ChatListScreenEvent) {
        when (event) {
            is ChatListScreenEvent.OnCreateChatClick -> {
                createChat(event.name)
            }

            is ChatListScreenEvent.OnOpenChatClick -> {
                commands.tryEmit(ChatListScreenCommand.OpenChat(event.chatId))
            }

            is ChatListScreenEvent.OnDeleteChatClick -> {
                deleteChat(event.chatId)
            }

            ChatListScreenEvent.OnOpenSettingsClick -> {
                commands.tryEmit(ChatListScreenCommand.OpenSettings)
            }
        }
    }

    private fun createChat(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return

        scope.launch(Dispatchers.IO) {
            val chat = Chat(
                id = UUID.randomUUID(),
                name = trimmed,
                time = System.currentTimeMillis(),
                parentId = null,
                taskContextId = null,
            )
            chatRepository.add(chat)
            commands.tryEmit(ChatListScreenCommand.OpenChat(chat.id))
        }
    }

    private fun deleteChat(chatId: UUID) {
        scope.launch(Dispatchers.IO) {
            chatRepository.deleteChat(chatId)
        }
    }
}
