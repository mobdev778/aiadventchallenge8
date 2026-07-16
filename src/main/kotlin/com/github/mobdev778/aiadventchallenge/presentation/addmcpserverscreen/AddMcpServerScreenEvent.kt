package com.github.mobdev778.aiadventchallenge.presentation.addmcpserverscreen

/**
 * События пользовательского интерфейса экрана добавления MCP-сервера.
 * Используются для обработки действий пользователя на соответствующем экране.
 */
sealed interface AddMcpServerScreenEvent {
    /**
     * Событие нажатия кнопки "Назад".
     * Инициирует переход на предыдущий экран или закрытие текущего.
     */
    data object OnBackClick : AddMcpServerScreenEvent

    /**
     * Событие изменения значения в поле "Имя сервера".
     *
     * @property value Новое значение имени сервера, введённое пользователем.
     */
    data class OnNameChange(val value: String) : AddMcpServerScreenEvent

    /**
     * Событие изменения значения в поле "URL сервера".
     *
     * @property value Новое значение URL сервера, введённое пользователем.
     */
    data class OnUrlChange(val value: String) : AddMcpServerScreenEvent

    /**
     * Событие нажатия кнопки "Добавить".
     * Запускает процесс добавления нового MCP-сервера с введёнными данными.
     */
    data object OnAddClick : AddMcpServerScreenEvent
}
