package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.composable

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.ChatScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model.ChatScreenState
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model.ContextManagementState

/**
 * Секция отображения списка сообщений чата в рамках [ColumnScope].
 *
 * Компонент объединяет автоматическую прокрутку к последнему сообщению
 * и рендеринг иерархического списка сообщений с поддержкой ветвления.
 * Используется как дочерний элемент вертикальной компоновки и должен
 * располагаться внутри [androidx.compose.foundation.layout.Column].
 *
 * Автоматическая прокрутка реализуется через [AutoScrollToBottom],
 * которая отслеживает изменения списка сообщений и гарантирует видимость
 * последнего элемента. Список сообщений рендерится с помощью [ChatMessageList],
 * при этом режим ветвления включается, если текущее состояние управления
 * контекстом соответствует [ContextManagementState.Branching].
 *
 * @param state Текущее состояние экрана чата, содержащее список сообщений
 *              и параметры управления контекстом.
 * @param listState Состояние прокрутки [LazyListState], используемое
 *                  для сохранения и управления позицией скролла внутри
 *                  списка сообщений.
 * @param onEvent Обработчик событий экрана чата, принимающий
 *                [ChatScreenEvent] для передачи пользовательских
 *                намерений в вышестоящий слой (например, ViewModel).
 */
@Composable
fun ColumnScope.ChatMessageSection(
    state: ChatScreenState,
    listState: LazyListState,
    onEvent: (ChatScreenEvent) -> Unit,
) {
    AutoScrollToBottom(
        listState = listState,
        messages = state.messages,
    )

    ChatMessageList(
        listState = listState,
        messages = state.messages,
        isBranchingEnabled = state.contextManagementState is ContextManagementState.Branching,
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f),
        onEvent = onEvent,
    )
}
