package com.github.mobdev778.aiadventchallenge.presentation.chatlistscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.github.mobdev778.aiadventchallenge.presentation.chatlistscreen.composable.ChatListScreenContent
import java.util.UUID

@Composable
fun ChatListScreen(
    stateHolder: ChatListScreenStateHolder,
    onOpenChat: (UUID) -> Unit,
    onOpenSettings: () -> Unit,
) {
    val chats by stateHolder.chats.collectAsState()

    ChatListScreenContent(
        chats = chats,
        onEvent = stateHolder::onEvent,
    )

    LaunchedEffect(stateHolder.commands) {
        stateHolder.commands.collect { command ->
            when (command) {
                is ChatListScreenCommand.OpenSettings -> {
                    onOpenSettings()
                }
                is ChatListScreenCommand.OpenChat -> {
                    onOpenChat(command.chatId)
                }

            }
        }
    }
}
