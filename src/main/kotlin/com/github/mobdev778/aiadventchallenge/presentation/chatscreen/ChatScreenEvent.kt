package com.github.mobdev778.aiadventchallenge.presentation.chatscreen

import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model.ChatUiMessage

/**
 * События экрана чата, описывающие все возможные действия пользователя.
 *
 * Используется для однонаправленной передачи намерений от UI-слоя к ViewModel,
 * что позволяет централизованно обрабатывать пользовательский ввод и изменять состояние экрана.
 *
 * Каждое событие представляет собой атомарное действие: навигацию, ввод текста,
 * управление сообщениями (раскрытие веток, клик по сообщению) и т.д.
 */
sealed interface ChatScreenEvent {
    /**
     * Нажатие на кнопку "Назад".
     */
    data object OnBackClick : ChatScreenEvent

    /**
     * Запрос на очистку всех сообщений в чате.
     */
    data object OnClearAllMessagesClick : ChatScreenEvent

    /**
     * Запрос на остановку автоматического воспроизведения сообщений.
     */
    data object OnStopAutoPlayClick : ChatScreenEvent

    /**
     * Нажатие на кнопку продолжения диалога (например, для генерации нового сообщения).
     */
    data object OnContinueDialogClick : ChatScreenEvent

    /**
     * Клик по конкретному сообщению в списке.
     *
     * @param message Сообщение, по которому был произведён клик. Содержит всю UI-модель,
     *                включая информацию о ветвлении и видимости.
     */
    data class OnMessageClicked(val message: ChatUiMessage) : ChatScreenEvent

    /**
     * Изменение текста в поле ввода сообщения.
     *
     * @param text Текущий текст, введённый пользователем.
     */
    data class OnInputTextChanged(val text: String) : ChatScreenEvent

    /**
     * Нажатие на кнопку отправки сообщения (ввод готов).
     */
    data object OnSendMessageClick : ChatScreenEvent

    /**
     * Переключение состояния раскрытия/сворачивания ветки дочерних сообщений.
     *
     * Используется для отображения иерархической структуры переписки.
     *
     * @param message Сообщение, для которого нужно изменить состояние раскрытия ветки.
     */
    data class OnMessageBranchToggle(val message: ChatUiMessage) : ChatScreenEvent

    /**
     * Клик по индикатору состояния задачи (например, прогресс генерации ответа).
     */
    data object OnTaskStateIndicatorClick : ChatScreenEvent
}
