package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen

/**
 * Запечатанный интерфейс, представляющий команды, которые могут быть отправлены из экрана настроек.
 * Каждая реализация описывает конкретное действие пользователя.
 */
sealed interface SettingsScreenCommand {
    /**
     * Команда для возврата на предыдущий экран.
     */
    data object Back : SettingsScreenCommand

    /**
     * Команда для открытия экрана профилей.
     */
    data object OpenProfiles : SettingsScreenCommand

    /**
     * Команда для открытия экрана RAG (Retrieval-Augmented Generation).
     */
    data object OpenRag : SettingsScreenCommand

    /**
     * Команда для открытия экрана MCP (Model Context Protocol).
     */
    data object OpenMcp : SettingsScreenCommand

    /**
     * Команда для открытия экрана "Мой MCP".
     */
    data object OpenMyMcp : SettingsScreenCommand
}
