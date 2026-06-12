package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.ChatScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model.ChatScreenState
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text
import kotlin.collections.lastIndex

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
        // 0) Limit indicator
        ChatLimitIndicator(
            state = state.tokenLimitState,
            modifier = Modifier.fillMaxWidth(),
        )

        // 1) Messages list
        val rootListState = rememberLazyListState()
        val lastMessage = state.messages.lastOrNull()
        if (lastMessage != null) {
            LaunchedEffect(lastMessage) {
                rootListState.animateScrollToItem(state.messages.lastIndex)
            }
        }

        ChatMessageList(
            listState = rootListState,
            messages = state.messages,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            onEvent = onEvent,
        )

        // 2) Clear all Messages
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            OutlinedButton(
                onClick = {
                    onEvent(ChatScreenEvent.OnClearAllMessagesClick)
                },
            ) {
                Text("❌ Clear all messages")
            }
        }

        // 3) Input area
        ChatInputArea(
            inputText = state.inputText,
            modifier = Modifier
                .fillMaxWidth(),
            onInputTextChanged = { text ->
                onEvent(ChatScreenEvent.OnInputTextChanged(text))
            },
            onSendClick = {
                onEvent(ChatScreenEvent.OnSendMessageClick)
            },
        )
    }
}