package com.github.mobdev778.aiadventchallenge.presentation.chatscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.composable.ChatInputArea
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.composable.ChatMessageList
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ChatScreen(
    stateHolder: ChatScreenStateHolder,
) {
    val state = stateHolder.uiState.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // 1) Messages list
        ChatMessageList(
            messages = state.messages,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        )

        // 2) Input area
        ChatInputArea(
            inputText = state.inputText,
            onInputTextChanged = stateHolder::onInputTextChanged,
            onSendClick = stateHolder::onSendClicked,
        )
    }
}

@Preview
@Composable
private fun ChatScreenPreview() {
    // Preview is not wired to DI/network.
}
