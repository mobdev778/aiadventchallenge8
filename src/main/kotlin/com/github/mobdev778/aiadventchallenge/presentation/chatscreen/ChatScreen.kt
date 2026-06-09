package com.github.mobdev778.aiadventchallenge.presentation.chatscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.composable.ChatScreenContent

@Composable
fun ChatScreen(
    stateHolder: ChatScreenStateHolder,
) {
    val state = stateHolder.uiState.collectAsState().value
    ChatScreenContent(
        state = state,
        onEvent = stateHolder::onEvent
    )
}
