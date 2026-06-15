package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model.ContextManagementState
import com.github.mobdev778.aiadventchallenge.presentation.common.ScreenHeader
import com.github.mobdev778.aiadventchallenge.presentation.common.TaskStateIndicator
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedButton(onClick = { onEvent(ChatScreenEvent.OnBackClick) }) {
                Text("Back")
            }
        }

        // 0) Limit indicator
        ContextManagementIndicator(
            state = state.contextManagementState,
            modifier = Modifier.fillMaxWidth(),
        )

        // 1) Chat name
        ScreenHeader(
            modifier = Modifier.fillMaxWidth(),
            text = state.chat.name,
        )

        // 1.1) Task state indicator
        val taskState = state.taskContext?.state
        if (taskState != null) {
            TaskStateIndicator(
                state = taskState,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEvent(ChatScreenEvent.OnTaskStateIndicatorClick) }
                    .padding(vertical = 4.dp),
            )
        }

        // 1) Messages list
        // We want to see the *bottom* of the last item (important for long messages).
        // `scrollToItem(index)` aligns the item to the top of the viewport, so we additionally
        // scroll by the remaining offset to reach the very bottom.
        val rootListState = rememberLazyListState()
        val lastMessage = state.messages.lastOrNull()
        if (lastMessage != null) {
            LaunchedEffect(lastMessage) {
                val lastIndex = state.messages.lastIndex

                // 1) Bring last item into composition.
                rootListState.scrollToItem(lastIndex)

                // 2) Then scroll to the very bottom of the list.
                // After `scrollToItem`, layout info is available and we can compute the remaining delta.
                val layoutInfo = rootListState.layoutInfo
                val viewportEnd = layoutInfo.viewportEndOffset
                val lastItem = layoutInfo.visibleItemsInfo.lastOrNull { it.index == lastIndex }

                if (lastItem != null) {
                    val lastItemBottom = lastItem.offset + lastItem.size
                    val deltaToBottom = lastItemBottom - viewportEnd
                    if (deltaToBottom > 0) {
                        rootListState.scrollBy(deltaToBottom.toFloat())
                    }
                }
            }
        }

        ChatMessageList(
            listState = rootListState,
            messages = state.messages,
            isBranchingEnabled = state.contextManagementState is ContextManagementState.Branching,
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