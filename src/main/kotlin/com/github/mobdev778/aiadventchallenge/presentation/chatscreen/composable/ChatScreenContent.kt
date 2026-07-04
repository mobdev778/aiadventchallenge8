package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.ChatScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model.ChatScreenState

@Composable
fun ChatScreenContent(
    state: ChatScreenState,
    onEvent: (ChatScreenEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ChatHeaderSection(state = state, onEvent = onEvent)

        val rootListState = rememberLazyListState()
        ChatMessageSection(
            state = state,
            listState = rootListState,
            onEvent = onEvent,
        )

        ChatActionButtons(state = state, onEvent = onEvent)

        ChatInputArea(
            inputText = state.inputText,
            modifier = Modifier.fillMaxWidth(),
            onInputTextChanged = { text ->
                onEvent(ChatScreenEvent.OnInputTextChanged(text))
            },
            onSendClick = { onEvent(ChatScreenEvent.OnSendMessageClick) },
        )
    }
}

