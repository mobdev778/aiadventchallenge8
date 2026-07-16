package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.ChatScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model.ChatScreenState
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text

/**
 * Компонент, отображающий панель кнопок действий для экрана чата.
 *
 * Позволяет очистить все сообщения, а также управлять автоматическим проигрыванием диалога:
 * запустить или поставить на паузу в зависимости от текущего состояния [state].
 *
 * @param state текущее состояние чата, содержащее флаг [ChatScreenState.autoPlay] для определения
 *              отображаемой кнопки управления проигрыванием.
 * @param onEvent колбэк для отправки событий [ChatScreenEvent], инициируемых нажатием на
 *                соответствующую кнопку.
 */
@Composable
fun ChatActionButtons(
    state: ChatScreenState,
    onEvent: (ChatScreenEvent) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedButton(
                onClick = { onEvent(ChatScreenEvent.OnClearAllMessagesClick) },
            ) {
                Text("❌ Clear all messages")
            }

            if (state.autoPlay) {
                OutlinedButton(
                    onClick = { onEvent(ChatScreenEvent.OnStopAutoPlayClick) },
                ) {
                    Text("Пауза ⏸\uFE0F")
                }
            } else {
                OutlinedButton(
                    onClick = { onEvent(ChatScreenEvent.OnContinueDialogClick) },
                ) {
                    Text("Продолжай ▶\uFE0F")
                }
            }
        }
    }
}
