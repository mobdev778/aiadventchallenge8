package com.github.mobdev778.aiadventchallenge.domain.agent.model

import com.github.mobdev778.aiadventchallenge.domain.task.TaskContext

data class AgentResponse(
    val agent: String,
    val request: AgentRequest,
    val message: String,
    val toolMessages: List<ToolResponse>,
    val requestTokens: Int,
    val responseTokens: Int,
    val taskContext: TaskContext?, // обновленный контекст задачи
)
