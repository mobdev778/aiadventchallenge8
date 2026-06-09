package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.domain.chathistory.ChatAuthor
import com.github.mobdev778.aiadventchallenge.domain.chathistory.ChatMessage
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.ChatScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.ChatScreenState
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text

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
        // 1) Messages list
        ChatMessageList(
            messages = state.messages,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
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

@Preview
@Composable
private fun ChatScreenContentPreview() {
    ChatScreenContent(
        state = ChatScreenState(
            messages = listOf(
                ChatMessage(
                    id = 1L,
                    text = "Hello!",
                    author = ChatAuthor.User,
                ),
                ChatMessage(
                    id = 2L,
                    text = "Hi! How can I help you today?",
                    author = ChatAuthor.Bot,
                ),
            ),
            inputText = "Write something…",
        ),
        onEvent = {},
    )
}