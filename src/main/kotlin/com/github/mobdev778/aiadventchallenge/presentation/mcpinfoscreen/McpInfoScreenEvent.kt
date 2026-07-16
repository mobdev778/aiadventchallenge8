package com.github.mobdev778.aiadventchallenge.presentation.mcpinfoscreen

/**
 * Запечатанный интерфейс, представляющий все возможные пользовательские события
 * на экране информации MCP. Используется для централизованной обработки действий
 * пользователя в рамках архитектуры unidirectional data flow (UDF).
 */
sealed interface McpInfoScreenEvent {
    /**
     * Событие нажатия на кнопку "Назад". Инициирует переход к предыдущему экрану
     * или закрытие текущего диалогового окна.
     */
    data object OnBackClick : McpInfoScreenEvent

    /**
     * Событие нажатия на кнопку проверки/подтверждения. Запускает процесс
     * верификации или принятия условий, связанных с информацией MCP.
     */
    data object OnCheckClick : McpInfoScreenEvent
}
