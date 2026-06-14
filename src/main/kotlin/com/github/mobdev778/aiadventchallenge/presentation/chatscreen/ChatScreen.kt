package com.github.mobdev778.aiadventchallenge.presentation.chatscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.composable.ChatScreenContent
import java.util.UUID

@Composable
fun ChatScreen(
    chatId: UUID,
    stateHolder: ChatScreenStateHolder,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    val state = stateHolder.uiState.collectAsState().value

    LaunchedEffect(chatId) {
        stateHolder.setChat(chatId)
    }

    ChatScreenContent(
        state = state,
        onEvent = stateHolder::onEvent,
    )

    LaunchedEffect(stateHolder.commands) {
        stateHolder.commands.collect { command ->
            when (command) {
                ChatScreenCommand.Back -> onBack()
                ChatScreenCommand.OpenSettings -> onOpenSettings()
            }
        }
    }
}
