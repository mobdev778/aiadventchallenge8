package com.github.mobdev778.aiadventchallenge.domain.agent.model

import java.util.UUID

data class AgentRequest(
    val chatId: UUID,
    val taskContextId: UUID?,
    val parentMessageId: UUID?,
    val time: Long,
    val query: String,
)
