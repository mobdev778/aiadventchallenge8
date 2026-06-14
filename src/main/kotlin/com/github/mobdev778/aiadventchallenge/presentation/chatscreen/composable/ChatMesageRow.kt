package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun ChatMessageRow(
    message: ChatUiMessage,
    isBranchingEnabled: Boolean,
    onEvent: (ChatScreenEvent) -> Unit
) {
    val botAnswerColor = Color(0xFF04D9FF)
    val stickyFactsColor = Color(0xFF8c6700)

    val type = message.message.type
    val insideWindow = message.insideWindow
    val alignment = when (type) {
        MessageType.User -> Alignment.CenterStart
        MessageType.StickyFacts -> Alignment.Center
        MessageType.Bot -> Alignment.CenterEnd
    }

    val borderColor = when (type) {
        MessageType.User -> Color.White
        MessageType.Bot -> botAnswerColor
        MessageType.StickyFacts -> stickyFactsColor
    }
        .copy(alpha = if (insideWindow) 1.0f else 0.3f)

    val textColor = when (type) {
        MessageType.User -> Color(0xFFFFFF).copy(alpha = if (insideWindow) 1.0f else 0.3f)
        MessageType.Bot -> botAnswerColor
        MessageType.StickyFacts -> stickyFactsColor
    }

    val tokenColor = Color(0xFFFF8C00).copy(alpha = if (insideWindow) 1.0f else 0.3f)

    val shape = RoundedCornerShape(12.dp)

    val rowPadding = when (type) {
        MessageType.User -> Modifier.padding(start = 8.dp, end = 24.dp, top = 4.dp, bottom = 4.dp)
        MessageType.StickyFacts -> Modifier.padding(start = 24.dp, end = 24.dp, top = 4.dp, bottom = 4.dp)
        MessageType.Bot -> Modifier.padding(start = 24.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(rowPadding)
            .clickable(
                onClick = {
                    onEvent(ChatScreenEvent.OnMessageClicked(message))
                }
            ),
        contentAlignment = alignment,
    ) {
        Column(
            modifier = Modifier
                .border(width = 1.dp, color = borderColor, shape = shape)
                // subtle fill so the border is visible on both light/dark themes
                .background(color = borderColor.copy(alpha = 0.06f), shape = shape)
                .padding(horizontal = 10.dp, vertical = 8.dp),
        ) {
            when (type) {
                MessageType.StickyFacts -> {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Sticky Facts",
                        color = stickyFactsColor,
                    )
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        orientation = Orientation.Horizontal,
                    )
                    Text(text = message.message.text, color = textColor)
                }

                else -> {
                    Text(text = message.message.text, color = textColor)

                    ChatSwitcher(
                        message = message,
                        isBranchingEnabled = isBranchingEnabled,
                        onEvent = onEvent
                    )

                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        orientation = Orientation.Horizontal,
                    )

                    Text(
                        text = "tokens: ${message.message.tokens}",
                        color = tokenColor,
                    )
                }
            }
        }
    }
}
