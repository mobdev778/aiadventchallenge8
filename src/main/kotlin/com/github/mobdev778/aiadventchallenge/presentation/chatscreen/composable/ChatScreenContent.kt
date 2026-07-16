package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.ChatScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model.ChatScreenState

/**
 * Главная компоновка экрана чата, объединяющая заголовок, список сообщений,
 * кнопки действий и область ввода.
 *
 * Функция собирает все основные части интерфейса и передаёт единый обработчик событий [onEvent]
 * в дочерние компоненты. Служит корневым элементом для экрана чата и определяет
 * вертикальную структуру с отступами.
 *
 * @param state текущее состояние экрана чата, содержащее все данные для отображения.
 * @param onEvent обработчик событий, который передаётся вложенным компонентам,
 *        позволяет централизованно реагировать на действия пользователя.
 */
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
        ChatHeaderSection(state = state, onEvent = onEvent)

        val rootListState = rememberLazyListState()
        ChatMessageSection(
            state = state,
            listState = rootListState,
            onEvent = onEvent,
        )

        ChatActionButtons(state = state, onEvent = onEvent)

        ChatInputArea(
            inputText = state.inputText,
            modifier = Modifier.fillMaxWidth(),
            onInputTextChanged = { text ->
                onEvent(ChatScreenEvent.OnInputTextChanged(text))
            },
            onSendClick = { onEvent(ChatScreenEvent.OnSendMessageClick) },
        )
    }
}
