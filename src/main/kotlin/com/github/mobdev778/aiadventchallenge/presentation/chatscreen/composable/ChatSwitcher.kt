package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.domain.chat.model.MessageType
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.ChatScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model.ChatUiMessage
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.Divider
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text

@Composable
fun ChatSwitcher(
    message: ChatUiMessage,
    isBranchingEnabled: Boolean,
    onEvent: (ChatScreenEvent) -> Unit,
) {
    // Branch switcher for bot messages
    if (message.message.type == MessageType.Bot && isBranchingEnabled) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val isBSelected = message.message.branchB

            OutlinedButton(
                onClick = {
                    if (isBSelected) onEvent(ChatScreenEvent.OnMessageBranchToggle(message = message))
                },
                enabled = true,
            ) {
                Text(
                    text = "чат A",
                    color = if (!isBSelected) Color.White else Color.Gray
                )
            }

            OutlinedButton(
                onClick = {
                    if (!isBSelected) onEvent(ChatScreenEvent.OnMessageBranchToggle(message = message))
                },
                enabled = true,
            ) {
                Text(
                    text = "чат B",
                    color = if (isBSelected) Color.White else Color.Gray
                )
            }
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            orientation = Orientation.Horizontal,
        )
    }
}
