package com.github.mobdev778.aiadventchallenge.presentation.mcpinfoscreen

sealed interface McpInfoScreenCommand {
    data object Back : McpInfoScreenCommand
}
