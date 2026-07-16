package com.github.mobdev778.aiadventchallenge.presentation.chatscreen

import java.util.UUID

/**
 * Запечатанный интерфейс, представляющий набор команд,
 * доступных для выполнения с экрана чата.
 *
 * Используется для передачи пользовательских действий
 * от UI-слоя к компонентам, управляющим навигацией и
 * бизнес-логикой, связанной с чатом.
 */
sealed interface ChatScreenCommand {

    /**
     * Команда возврата на предыдущий экран.
     */
    data object Back : ChatScreenCommand

    /**
     * Команда открытия экрана настроек.
     */
    data object OpenSettings : ChatScreenCommand

    /**
     * Команда открытия контекста конкретной задачи.
     *
     * @param taskContextId Уникальный идентификатор контекста задачи,
     *   которую необходимо отобразить.
     * @param chatId Уникальный идентификатор чата, в рамках которого
     *   был инициирован запрос на просмотр контекста задачи.
     */
    data class OpenTaskContext(
        val taskContextId: UUID,
        val chatId: UUID
    ) : ChatScreenCommand
}
