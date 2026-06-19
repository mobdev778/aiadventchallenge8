package com.github.mobdev778.aiadventchallenge.presentation.chatscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.composable.ChatScreenContent
import java.util.UUID
import org.koin.java.KoinJavaComponent.inject

@Composable
fun ChatScreen(
    chatId: UUID,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenTaskContext: (taskContextId: UUID, chatId: UUID) -> Unit,
) {
    val stateHolder = remember(chatId) {
        inject<ChatScreenStateHolder>(ChatScreenStateHolder::class.java).value
    }
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
                is ChatScreenCommand.OpenTaskContext -> onOpenTaskContext(
                    command.taskContextId,
                    command.chatId,
                )
            }
        }
    }
}
