package com.github.mobdev778.aiadventchallenge.domain.mcpserver.model

import java.util.UUID

data class McpServer(
    val id: UUID,
    val active: Boolean,
    val name: String,
    val url: String,
)
