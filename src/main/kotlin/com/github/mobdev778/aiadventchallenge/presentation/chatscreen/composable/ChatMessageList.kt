package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.ChatScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model.ChatUiMessage

@Composable
fun ChatMessageList(
    listState: LazyListState = rememberLazyListState(),
    messages: List<ChatUiMessage>,
    modifier: Modifier = Modifier,
    onEvent: (ChatScreenEvent) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = messages,
            key = { it.message.id },
        ) { message ->
            Column {
                ChatMessageRow(
                    message = message,
                    onEvent = onEvent,
                )

                // IMPORTANT:
                // Nested LazyColumn inside LazyColumn item causes infinite height constraints crash.
                // Render children as a regular Column (non-scrollable) inside the parent list item.
                if (message.expanded && message.children.isNotEmpty()) {
                    ChatMessageChildren(
                        messages = message.children,
                        modifier = Modifier.padding(start = 24.dp),
                        onEvent = onEvent,
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatMessageChildren(
    messages: List<ChatUiMessage>,
    modifier: Modifier = Modifier,
    onEvent: (ChatScreenEvent) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        messages.forEach { message ->
            ChatMessageRow(
                message = message,
                onEvent = onEvent,
            )

            if (message.expanded && message.children.isNotEmpty()) {
                ChatMessageChildren(
                    messages = message.children,
                    modifier = Modifier.padding(start = 24.dp),
                    onEvent = onEvent,
                )
            }
        }
    }
}