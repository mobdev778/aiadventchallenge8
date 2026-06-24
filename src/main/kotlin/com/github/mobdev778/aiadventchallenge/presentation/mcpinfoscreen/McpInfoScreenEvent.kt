package com.github.mobdev778.aiadventchallenge.presentation.mcpinfoscreen

sealed interface McpInfoScreenEvent {
    data object OnBackClick : McpInfoScreenEvent
    data object OnCheckClick : McpInfoScreenEvent
}
