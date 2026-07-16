package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen

import com.github.mobdev778.aiadventchallenge.domain.settings.model.ContextManagementType

/**
 * Запечатанный интерфейс, представляющий события экрана настроек.
 * Каждое событие соответствует действию пользователя, которое должно быть обработано
 * ViewModel или другим компонентом слоя представления.
 */
sealed interface SettingsScreenEvent {

    /**
     * Событие нажатия кнопки «Назад».
     */
    data object OnBackClick : SettingsScreenEvent

    /**
     * Событие открытия экрана профилей.
     */
    data object OnOpenProfilesClick : SettingsScreenEvent

    /**
     * Событие открытия экрана RAG.
     */
    data object OnOpenRagClick : SettingsScreenEvent

    /**
     * Событие открытия экрана MCP.
     */
    data object OnOpenMcpClick : SettingsScreenEvent

    /**
     * Событие открытия экрана «Мои MCP».
     */
    data object OnOpenMyMcpClick : SettingsScreenEvent

    /**
     * Событие изменения выбранного типа управления контекстом.
     *
     * @param type новый тип управления контекстом, выбранный пользователем.
     */
    data class OnContextManagementTypeChanged(val type: ContextManagementType) : SettingsScreenEvent

    /**
     * Событие изменения значения максимального количества сообщений.
     *
     * @param value строковое представление нового значения.
     */
    data class OnMaxMessagesChanged(val value: String) : SettingsScreenEvent

    /**
     * Событие изменения значения максимального количества токенов.
     *
     * @param value строковое представление нового значения.
     */
    data class OnMaxTokensChanged(val value: String) : SettingsScreenEvent

    /**
     * Событие изменения максимального количества сообщений для закреплённых фактов.
     *
     * @param value строковое представление нового значения.
     */
    data class OnStickyFactsMaxMessagesChanged(val value: String) : SettingsScreenEvent

    /**
     * Событие изменения максимального количества сообщений для рекурсивного суммирования.
     *
     * @param value строковое представление нового значения.
     */
    data class OnRecursiveSummationMaxMessagesChanged(val value: String) : SettingsScreenEvent

    /**
     * Событие изменения значения API-ключа.
     *
     * @param value строковое представление нового значения API-ключа.
     */
    data class OnApiKeyChanged(val value: String) : SettingsScreenEvent

    /**
     * Событие изменения базового URL.
     *
     * @param value строковое представление нового значения базового URL.
     */
    data class OnBaseUrlChanged(val value: String) : SettingsScreenEvent

    /**
     * Событие изменения базовой модели.
     *
     * @param value строковое представление нового имени модели.
     */
    data class OnBaseModelChanged(val value: String) : SettingsScreenEvent

    /**
     * Событие нажатия кнопки «Сохранить».
     */
    data object OnSaveClick : SettingsScreenEvent

    /**
     * Событие нажатия кнопки «Сбросить».
     */
    data object OnResetClick : SettingsScreenEvent
}
