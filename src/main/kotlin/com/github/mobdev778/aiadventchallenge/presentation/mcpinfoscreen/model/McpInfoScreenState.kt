package com.github.mobdev778.aiadventchallenge.presentation.mcpinfoscreen.model

import androidx.compose.runtime.Immutable
import com.github.mobdev778.aiadventchallenge.domain.mcpserver.model.McpServer

@Immutable
data class McpInfoScreenState(
    val server: McpServer? = null,
    val isLoading: Boolean = false,
    val toolsText: String = "",
)
