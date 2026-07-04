package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.composable

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.ChatScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model.ChatScreenState
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model.ContextManagementState

@Composable
fun ColumnScope.ChatMessageSection(
    state: ChatScreenState,
    listState: LazyListState,
    onEvent: (ChatScreenEvent) -> Unit,
) {
    AutoScrollToBottom(
        listState = listState,
        messages = state.messages,
    )

    ChatMessageList(
        listState = listState,
        messages = state.messages,
        isBranchingEnabled = state.contextManagementState is ContextManagementState.Branching,
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        onEvent = onEvent,
    )
}
