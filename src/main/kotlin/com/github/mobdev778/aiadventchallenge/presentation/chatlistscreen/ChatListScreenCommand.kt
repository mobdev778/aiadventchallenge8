package com.github.mobdev778.aiadventchallenge.presentation.chatlistscreen

import java.util.UUID

/**
 * Запечатанный интерфейс, описывающий команды, которые могут быть инициированы
 * на экране списка чатов. Каждая команда представляет собой намерение
 * пользовательского взаимодействия для навигации или выполнения действия.
 */
sealed interface ChatListScreenCommand {
    /**
     * Команда открытия конкретного чата по его уникальному идентификатору.
     *
     * @property chatId Уникальный идентификатор чата, который требуется открыть.
     */
    data class OpenChat(val chatId: UUID) : ChatListScreenCommand

    /**
     * Команда открытия экрана настроек приложения.
     */
    data object OpenSettings : ChatListScreenCommand
}
