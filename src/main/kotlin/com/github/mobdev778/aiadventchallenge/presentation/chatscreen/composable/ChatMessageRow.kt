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
import com.github.mobdev778.aiadventchallenge.domain.chat.model.MessageType
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.ChatScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model.ChatUiMessage
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.Divider
import org.jetbrains.jewel.ui.component.Text
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Suppress("MagicNumber")
private val BotAnswerColor = Color(0xFF04D9FF)
@Suppress("MagicNumber")
private val StickyFactsColor = Color(0xFF8c6700)
@Suppress("MagicNumber")
private val UserTextColor = Color(0xFFFFFF)
@Suppress("MagicNumber")
private val TokenColor = Color(0xFFFF8C00)

private data class MessageStyle(
    val alignment: Alignment,
    val borderColor: Color,
    val textColor: Color,
    val tokenColor: Color,
    val rowPadding: Modifier,
)

@Suppress("MagicNumber")
private fun getMessageStyle(type: MessageType, insideWindow: Boolean): MessageStyle {
    val alpha = if (insideWindow) 1.0f else 0.3f
    return when (type) {
        MessageType.User -> MessageStyle(
            alignment = Alignment.CenterStart,
            borderColor = Color.White.copy(alpha = alpha),
            textColor = UserTextColor.copy(alpha = alpha),
            tokenColor = TokenColor.copy(alpha = alpha),
            rowPadding = Modifier.padding(start = 8.dp, end = 24.dp, top = 4.dp, bottom = 4.dp),
        )
        MessageType.StickyFacts -> MessageStyle(
            alignment = Alignment.Center,
            borderColor = StickyFactsColor.copy(alpha = alpha),
            textColor = StickyFactsColor,
            tokenColor = TokenColor.copy(alpha = alpha),
            rowPadding = Modifier.padding(start = 24.dp, end = 24.dp, top = 4.dp, bottom = 4.dp),
        )
        MessageType.Bot, MessageType.Tool -> MessageStyle(
            alignment = Alignment.CenterEnd,
            borderColor = BotAnswerColor.copy(alpha = alpha),
            textColor = BotAnswerColor,
            tokenColor = TokenColor.copy(alpha = alpha),
            rowPadding = Modifier.padding(start = 24.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
        )
    }
}

private const val TIME_PATTERN = "dd.MM.yyyy HH:mm"
private val timeFormatter = DateTimeFormatter.ofPattern(TIME_PATTERN)
    .withZone(ZoneId.systemDefault())

private fun formatMessageTime(epochMillis: Long): String {
    val formatted = timeFormatter.format(Instant.ofEpochMilli(epochMillis))
    return "$formatted"
}

@Composable
fun ChatMessageRow(
    message: ChatUiMessage,
    isBranchingEnabled: Boolean,
    onEvent: (ChatScreenEvent) -> Unit
) {
    val style = getMessageStyle(message.message.type, message.insideWindow)
    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(style.rowPadding)
            .clickable(
                onClick = {
                    onEvent(ChatScreenEvent.OnMessageClicked(message))
                }
            ),
        contentAlignment = style.alignment,
    ) {
        Column(
            modifier = Modifier
                .border(width = 1.dp, color = style.borderColor, shape = shape)
                // subtle fill so the border is visible on both light/dark themes
                .background(color = style.borderColor.copy(alpha = 0.06f), shape = shape)
                .padding(horizontal = 10.dp, vertical = 8.dp),
        ) {
            Text(
                text = formatMessageTime(message.time),
                color = style.borderColor,
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                orientation = Orientation.Horizontal,
            )

            when (message.message.type) {
                MessageType.StickyFacts -> {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "Sticky Facts",
                        color = StickyFactsColor,
                    )
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        orientation = Orientation.Horizontal,
                    )
                    MarkdownText(text = message.message.text, color = style.textColor)
                }

                else -> {
                    MarkdownText(text = message.message.text, color = style.textColor)

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
                        color = style.tokenColor,
                    )
                }
            }
        }
    }
}
