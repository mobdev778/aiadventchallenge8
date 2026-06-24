package com.github.mobdev778.aiadventchallenge.presentation.mcpserverlistscreen

sealed interface McpServerListScreenCommand {
    data object Back : McpServerListScreenCommand
    data object OpenAddServer : McpServerListScreenCommand
    data class OpenServerInfo(val serverId: java.util.UUID) : McpServerListScreenCommand
}
