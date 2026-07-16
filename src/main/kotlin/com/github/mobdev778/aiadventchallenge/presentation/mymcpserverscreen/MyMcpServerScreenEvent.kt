package com.github.mobdev778.aiadventchallenge.presentation.mymcpserverscreen

/**
 * События, которые могут быть инициированы пользователем на экране «Мои MCP-серверы».
 * Каждое событие обрабатывается соответствующей ViewModel или компонентом навигации.
 */
sealed interface MyMcpServerScreenEvent {
    /**
     * Пользователь нажал кнопку «Назад» — запрос на возврат к предыдущему экрану.
     */
    data object OnBackClick : MyMcpServerScreenEvent

    /**
     * Пользователь нажал кнопку «Запустить» для конкретного MCP-сервера.
     *
     * @param name Имя сервера, который требуется запустить.
     */
    data class OnStartClick(val name: String) : MyMcpServerScreenEvent

    /**
     * Пользователь нажал кнопку «Остановить» для конкретного MCP-сервера.
     *
     * @param name Имя сервера, который требуется остановить.
     */
    data class OnStopClick(val name: String) : MyMcpServerScreenEvent
}
