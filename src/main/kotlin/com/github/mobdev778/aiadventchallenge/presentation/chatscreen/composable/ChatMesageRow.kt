package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.domain.chathistory.model.ChatAuthor
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.ChatScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model.ChatUiMessage
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.Divider
import org.jetbrains.jewel.ui.component.Text

@Composable
fun ChatMessageRow(
    message: ChatUiMessage,
    onEvent: (ChatScreenEvent) -> Unit
) {
    val author = message.message.author
    val insideWindow = message.insideWindow
    val alignment = when (author) {
        ChatAuthor.User -> Alignment.CenterStart
        ChatAuthor.Bot -> Alignment.CenterEnd
    }

    val borderColor = when (author) {
        ChatAuthor.User -> Color.White
        ChatAuthor.Bot -> Color(0xFFBDBDBD) // light gray
    }
        .copy(alpha = if (insideWindow) 1.0f else 0.3f)

    val textColor = Color(0xFFFFFF)
        .copy(alpha = if (insideWindow) 1.0f else 0.3f)

    val tokenColor = Color(0xFFFF8C00)
        .copy(alpha = if (insideWindow) 1.0f else 0.3f)

    val shape = RoundedCornerShape(12.dp)

    val rowPadding = when (author) {
        ChatAuthor.User -> Modifier.padding(start = 8.dp, end = 24.dp, top = 4.dp, bottom = 4.dp)
        ChatAuthor.Bot -> Modifier.padding(start = 24.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
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
            Text(text = message.message.text, color = textColor)

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
