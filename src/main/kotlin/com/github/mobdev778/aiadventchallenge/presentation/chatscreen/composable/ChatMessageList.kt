package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.ChatScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model.ChatUiMessage

/**
 * Компонент отображения списка сообщений чата с поддержкой иерархической структуры ветвления.
 *
 * Использует [LazyColumn] для эффективного рендеринга больших списков. Каждое сообщение представлено
 * [ChatMessageRow]. Состояние прокрутки управляется через [LazyListState], что позволяет сохранять
 * позицию при обновлениях контента (например, при автопрокрутке).
 *
 * Поддерживает включение/отключение визуального отображения ветвления сообщений через параметр
 * [isBranchingEnabled]. События пользовательского взаимодействия, такие как клики по сообщениям,
 * раскрытие веток и т.д., передаются в [onEvent] в виде объектов [ChatScreenEvent], что обеспечивает
 * однонаправленный поток данных.
 *
 * @param listState Состояние прокрутки LazyList. По умолчанию создаётся новый [LazyListState].
 * @param messages Список UI-моделей сообщений [ChatUiMessage], отображаемых в текущий момент.
 *                 Может включать родительские и дочерние сообщения в зависимости от состояния раскрытия веток.
 * @param isBranchingEnabled Флаг, определяющий, разрешено ли отображение ветвления (например, альтернативных ответов).
 * @param modifier Модификатор компоновки, применяемый к контейнеру LazyColumn.
 * @param onEvent Лямбда-обработчик событий экрана чата. Принимает [ChatScreenEvent] для передачи намерений
 *                в вышестоящий компонент (обычно ViewModel).
 */
@Composable
fun ChatMessageList(
    listState: LazyListState = rememberLazyListState(),
    messages: List<ChatUiMessage>,
    isBranchingEnabled: Boolean,
    modifier: Modifier = Modifier,
    onEvent: (ChatScreenEvent) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        itemsIndexed(
            items = messages,
            key = { index, ui -> ui.lazyKey(parentPath = "root", siblingIndex = index) },
        ) { index, message ->
            Column {
                ChatMessageRow(
                    message = message,
                    isBranchingEnabled = isBranchingEnabled,
                    onEvent = onEvent,
                )
            }
        }
    }
}

/**
 * Генерирует уникальный строковый ключ для элемента [ChatUiMessage] в списке, обеспечивающий стабильность
 * при изменениях порядка или состава сообщений.
 *
 * Ключ строится на основе пути родительского сообщения, идентификатора сообщения, его ранга и позиции
 * среди сиблингов, что позволяет Compose эффективно переиспользовать composable-элементы даже в сложных
 * иерархических структурах с ветвлением.
 *
 * @param parentPath Строковое представление пути родительского сообщения (например, "root" для корневых).
 * @param siblingIndex Индекс сообщения среди соседей на текущем уровне вложенности.
 * @return Уникальный ключ в формате `parentPath/message.id:rank:siblingIndex`.
 */
private fun ChatUiMessage.lazyKey(parentPath: String, siblingIndex: Int): String {
    return "$parentPath/${message.id}:${rank}:$siblingIndex"
}
