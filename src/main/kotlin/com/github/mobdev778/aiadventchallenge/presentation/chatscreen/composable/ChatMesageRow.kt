package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.domain.chathistory.ChatAuthor
import com.github.mobdev778.aiadventchallenge.domain.chathistory.ChatMessage
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.jewel.ui.component.Text

@Composable
fun ChatMessageRow(
    message: ChatMessage,
) {
    val alignment = when (message.author) {
        ChatAuthor.User -> Alignment.CenterStart
        ChatAuthor.Bot -> Alignment.CenterEnd
    }

    val borderColor = when (message.author) {
        ChatAuthor.User -> Color.White
        ChatAuthor.Bot -> Color(0xFFBDBDBD) // light gray
    }

    val shape = RoundedCornerShape(12.dp)

    val rowPadding = when (message.author) {
        ChatAuthor.User -> Modifier.padding(start = 8.dp, end = 24.dp, top = 4.dp, bottom = 4.dp)
        ChatAuthor.Bot -> Modifier.padding(start = 24.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(rowPadding),
        contentAlignment = alignment,
    ) {
        Text(
            text = message.text,
            modifier = Modifier
                .border(width = 1.dp, color = borderColor, shape = shape)
                // subtle fill so the border is visible on both light/dark themes
                .background(color = borderColor.copy(alpha = 0.06f), shape = shape)
                .padding(horizontal = 10.dp, vertical = 8.dp),
        )
    }
}

@Preview
@Composable
private fun ChatMessageRowPreviewUser() {
    ChatMessageRow(
        message = ChatMessage(
            id = 1,
            text = "User message",
            author = ChatAuthor.User,
        ),
    )
}

@Preview
@Composable
private fun ChatMessageRowPreviewBot() {
    ChatMessageRow(
        message = ChatMessage(
            id = 2,
            text = "Bot message",
            author = ChatAuthor.Bot,
        ),
    )
}
