package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.composable

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model.ChatUiMessage

/**
 * Автоматически прокручивает список сообщений к последнему элементу, обеспечивая его полную видимость
 * в области просмотра. При появлении нового сообщения (изменении [messages]) скроллит список так,
 * чтобы последнее сообщение было полностью отображено, не оставляя его частично скрытым за нижней границей.
 *
 * @param listState состояние прокрутки [LazyListState], управляющее позицией списка.
 * @param messages список сообщений [ChatUiMessage], по последнему элементу которого определяется момент
 * автоматической прокрутки.
 */
@Composable
fun AutoScrollToBottom(
    listState: LazyListState,
    messages: List<ChatUiMessage>,
) {
    val lastMessage = messages.lastOrNull()
    if (lastMessage != null) {
        LaunchedEffect(lastMessage) {
            val lastIndex = messages.lastIndex
            listState.scrollToItem(lastIndex)
            val layoutInfo = listState.layoutInfo
            val viewportEnd = layoutInfo.viewportEndOffset
            val lastItem = layoutInfo.visibleItemsInfo.lastOrNull { it.index == lastIndex }
            if (lastItem != null) {
                val lastItemBottom = lastItem.offset + lastItem.size
                val deltaToBottom = lastItemBottom - viewportEnd
                if (deltaToBottom > 0) {
                    listState.scrollBy(deltaToBottom.toFloat())
                }
            }
        }
    }
}
