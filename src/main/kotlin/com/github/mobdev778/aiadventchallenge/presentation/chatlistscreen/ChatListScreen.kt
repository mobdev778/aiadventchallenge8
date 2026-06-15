package com.github.mobdev778.aiadventchallenge.presentation.chatlistscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.github.mobdev778.aiadventchallenge.presentation.chatlistscreen.composable.ChatListScreenContent
import org.koin.java.KoinJavaComponent.inject
import java.util.UUID

@Composable
fun ChatListScreen(
    onOpenChat: (UUID) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenProfiles: () -> Unit,
) {
    val stateHolder = remember {
        inject<ChatListScreenStateHolder>(ChatListScreenStateHolder::class.java).value
    }
    val chats by stateHolder.chats.collectAsState()

    ChatListScreenContent(
        chats = chats,
        onEvent = stateHolder::onEvent,
    )

    LaunchedEffect(stateHolder.commands) {
        stateHolder.commands.collect { command ->
            when (command) {
                ChatListScreenCommand.OpenSettings -> {
                    onOpenSettings()
                }

                ChatListScreenCommand.OpenProfiles -> {
                    onOpenProfiles()
                }

                is ChatListScreenCommand.OpenChat -> {
                    onOpenChat(command.chatId)
                }
            }
        }
    }
}
