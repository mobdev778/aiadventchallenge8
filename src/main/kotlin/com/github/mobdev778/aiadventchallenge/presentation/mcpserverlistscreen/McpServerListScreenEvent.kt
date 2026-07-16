package com.github.mobdev778.aiadventchallenge.presentation.mcpserverlistscreen

import java.util.UUID

/**
 * Запечатанный интерфейс, представляющий все возможные события пользовательского взаимодействия
 * на экране списка MCP-серверов. Используется для однонаправленной передачи событий от UI-слоя
 * к ViewModel в архитектуре UDF (Unidirectional Data Flow).
 *
 * Каждое событие инкапсулирует необходимые данные для обработки конкретного действия пользователя.
 */
sealed interface McpServerListScreenEvent {

    /**
     * Событие нажатия на кнопку "Назад" для возврата к предыдущему экрану.
     */
    data object OnBackClick : McpServerListScreenEvent

    /**
     * Событие нажатия на кнопку "Добавить" для перехода к экрану создания нового MCP-сервера.
     */
    data object OnAddClick : McpServerListScreenEvent

    /**
     * Событие переключения состояния активности (включён/выключен) конкретного MCP-сервера.
     *
     * @param serverId Уникальный идентификатор сервера, состояние которого изменяется.
     * @param active Новое состояние активности: `true` — сервер включён, `false` — выключен.
     */
    data class OnActiveChanged(val serverId: UUID, val active: Boolean) : McpServerListScreenEvent

    /**
     * Событие нажатия на элемент списка, соответствующего конкретному MCP-серверу.
     * Обычно приводит к переходу на экран детальной информации или редактирования сервера.
     *
     * @param serverId Уникальный идентификатор выбранного сервера.
     */
    data class OnServerClick(val serverId: UUID) : McpServerListScreenEvent

    /**
     * Событие нажатия на кнопку удаления конкретного MCP-сервера из списка.
     *
     * @param serverId Уникальный идентификатор сервера, который требуется удалить.
     */
    data class OnDeleteClick(val serverId: UUID) : McpServerListScreenEvent
}
