package com.github.mobdev778.aiadventchallenge.presentation.mcpserverlistscreen

/**
 * Запечатанный интерфейс, представляющий команды, доступные на экране списка MCP-серверов.
 * Каждая команда инкапсулирует намерение пользователя и используется для передачи
 * событий от UI-слоя к ViewModel или соответствующему обработчику навигации.
 */
sealed interface McpServerListScreenCommand {

    /**
     * Команда возврата на предыдущий экран.
     */
    data object Back : McpServerListScreenCommand

    /**
     * Команда открытия экрана добавления нового MCP-сервера.
     */
    data object OpenAddServer : McpServerListScreenCommand

    /**
     * Команда открытия экрана с подробной информацией о выбранном MCP-сервере.
     *
     * @param serverId Уникальный идентификатор сервера, информацию о котором необходимо отобразить.
     */
    data class OpenServerInfo(val serverId: java.util.UUID) : McpServerListScreenCommand
}
