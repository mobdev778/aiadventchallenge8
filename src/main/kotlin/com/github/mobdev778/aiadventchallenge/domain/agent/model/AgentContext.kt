package com.github.mobdev778.aiadventchallenge.domain.agent.model

import com.github.mobdev778.aiadventchallenge.domain.chat.model.ChatMessage
import com.github.mobdev778.aiadventchallenge.domain.invariant.Invariant
import com.github.mobdev778.aiadventchallenge.domain.profile.model.Profile
import com.github.mobdev778.aiadventchallenge.domain.task.TaskContext
import io.modelcontextprotocol.kotlin.sdk.types.Tool

data class AgentContext(
    val profile: Profile,
    val taskContext: TaskContext?,
    val windowMessages: List<ChatMessage>,
    val invariants: List<Invariant>,
    val tools: List<Tool>,
)