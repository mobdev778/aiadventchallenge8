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

/** Цвет рамки и основного акцента для сообщений ассистента (Bot/Tool). */
@Suppress("MagicNumber")
private val BotAnswerColor = Color(0xFF04D9FF)

/** Цвет рамки и текста для закреплённых фактов (StickyFacts). */
@Suppress("MagicNumber")
private val StickyFactsColor = Color(0xFF8c6700)

/** Цвет текста пользовательских сообщений. */
@Suppress("MagicNumber")
private val UserTextColor = Color(0xFFFFFFFF)

/** Цвет текста для отображения количества токенов. */
@Suppress("MagicNumber")
private val TokenColor = Color(0xFFFF8C00)

/**
 * Вспомогательная модель, описывающая визуальный стиль отдельного сообщения.
 * Содержит параметры выравнивания, цвета и отступов в зависимости от типа сообщения.
 *
 * @property alignment выравнивание контейнера сообщения внутри строки.
 * @property borderColor цвет рамки.
 * @property textColor цвет основного текста.
 * @property tokenColor цвет метки с токенами.
 * @property rowPadding паддинги вокруг сообщения.
 */
private data class MessageStyle(
    val alignment: Alignment,
    val borderColor: Color,
    val textColor: Color,
    val tokenColor: Color,
    val rowPadding: Modifier,
)

/**
 * Возвращает стиль отображения для сообщения в зависимости от его типа и видимости.
 *
 * Если сообщение находится вне видимой области экрана ([insideWindow] = false),
 * ко всем цветам применяется коэффициент прозрачности 0.3, чтобы снизить нагрузку на отрисовку.
 *
 * @param type тип сообщения (User, Bot, Tool, StickyFacts).
 * @param insideWindow флаг нахождения сообщения в видимой области списка.
 * @return [MessageStyle] с подобранными параметрами.
 */
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

/**
 * Форматирует Unix-время (в миллисекундах) в строку вида `dd.MM.yyyy HH:mm`
 * с использованием локального часового пояса.
 *
 * @param epochMillis временная метка в миллисекундах.
 * @return отформатированная строка времени.
 */
private fun formatMessageTime(epochMillis: Long): String {
    val formatted = timeFormatter.format(Instant.ofEpochMilli(epochMillis))
    return "$formatted"
}

/**
 * Composable-компонент, отображающий отдельное сообщение в списке чата.
 *
 * В зависимости от типа сообщения ([MessageType]) применяется различное выравнивание,
 * цветовая схема и отступы. Сообщения пользователя выравниваются по левому краю,
 * ответы ассистента и вызовы инструментов — по правому, а закреплённые факты — по центру.
 * Если сообщение находится вне видимой области ([ChatUiMessage.insideWindow] == false),
 * прозрачность всех элементов снижается до 30% для уменьшения нагрузки на рендеринг.
 *
 * Компонент также отображает:
 * - отформатированное время создания сообщения;
 * - визуальный разделитель;
 * - Markdown-содержимое сообщения через [MarkdownText];
 * - для сообщений типа [MessageType.StickyFacts] дополнительно выводится заголовок "Sticky Facts";
 * - для сообщений типа [MessageType.Bot] и [MessageType.Tool] — переключатель ветвления [ChatSwitcher]
 *   и количество затраченных токенов;
 * - весь блок кликабелен: по нажатию генерируется событие [ChatScreenEvent.OnMessageClicked].
 *
 * @param message UI-модель сообщения, содержащая все необходимые данные.
 * @param isBranchingEnabled флаг, разрешающий переключение веток диалога. Если `false`,
 *                           [ChatSwitcher] не показывается для ботовых сообщений.
 * @param onEvent лямбда-обработчик событий экрана чата, используется для передачи пользовательских действий.
 */
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
