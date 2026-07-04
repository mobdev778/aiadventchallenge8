package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
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
    isBranchingEnabled: Boolean,
    modifier: Modifier = Modifier,
    onEvent: (ChatScreenEvent) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        itemsIndexed(
            items = messages,
            key = { index, ui -> ui.lazyKey(parentPath = "root", siblingIndex = index) },
        ) { index, message ->
            Column {
                ChatMessageRow(
                    message = message,
                    isBranchingEnabled = isBranchingEnabled,
                    onEvent = onEvent,
                )
            }
        }
    }
}

private fun ChatUiMessage.lazyKey(parentPath: String, siblingIndex: Int): String {
    return "$parentPath/${message.id}:${rank}:$siblingIndex"
}
