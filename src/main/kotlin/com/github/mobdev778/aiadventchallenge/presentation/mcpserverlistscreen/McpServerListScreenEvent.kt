package com.github.mobdev778.aiadventchallenge.presentation.mcpserverlistscreen

import java.util.UUID

sealed interface McpServerListScreenEvent {
    data object OnBackClick : McpServerListScreenEvent
    data object OnAddClick : McpServerListScreenEvent
    data class OnActiveChanged(val serverId: UUID, val active: Boolean) : McpServerListScreenEvent
    data class OnServerClick(val serverId: UUID) : McpServerListScreenEvent
    data class OnDeleteClick(val serverId: UUID) : McpServerListScreenEvent
}
