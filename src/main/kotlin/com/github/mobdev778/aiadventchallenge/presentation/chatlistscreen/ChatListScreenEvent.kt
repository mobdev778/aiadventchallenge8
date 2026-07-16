package com.github.mobdev778.aiadventchallenge.presentation.chatlistscreen

import java.util.UUID

/**
 * Запечатанный интерфейс, описывающий все возможные пользовательские события
 * на экране списка чатов. Используется для передачи событий от UI-слоя к
 * ViewModel, что позволяет централизованно обрабатывать навигацию и
 * операции с чатами.
 */
sealed interface ChatListScreenEvent {

    /**
     * Событие, возникающее при нажатии на кнопку открытия экрана настроек.
     */
    data object OnOpenSettingsClick : ChatListScreenEvent

    /**
     * Событие, возникающее при нажатии на конкретный чат для его открытия.
     *
     * @param chatId Уникальный идентификатор чата, который требуется открыть.
     */
    data class OnOpenChatClick(val chatId: UUID) : ChatListScreenEvent

    /**
     * Событие, возникающее при создании нового чата.
     *
     * @param name Имя создаваемого чата, заданное пользователем.
     */
    data class OnCreateChatClick(val name: String) : ChatListScreenEvent

    /**
     * Событие, возникающее при запросе на удаление чата.
     *
     * @param chatId Уникальный идентификатор чата, который требуется удалить.
     */
    data class OnDeleteChatClick(val chatId: UUID) : ChatListScreenEvent
}
