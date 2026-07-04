package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.ChatScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model.ChatScreenState
import com.github.mobdev778.aiadventchallenge.presentation.common.ScreenHeader
import com.github.mobdev778.aiadventchallenge.presentation.common.TaskStateIndicator
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text

@Composable
fun ChatHeaderSection(
    state: ChatScreenState,
    onEvent: (ChatScreenEvent) -> Unit,
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

    ContextManagementIndicator(
        state = state.contextManagementState,
        modifier = Modifier.fillMaxWidth(),
    )

    ScreenHeader(
        modifier = Modifier.fillMaxWidth(),
        text = state.chat.name,
    )

    val taskState = state.taskContext?.state
    if (taskState != null) {
        TaskStateIndicator(
            autoPlay = state.autoPlay,
            state = taskState,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEvent(ChatScreenEvent.OnTaskStateIndicatorClick) }
                .padding(vertical = 4.dp),
        )
    }
}
