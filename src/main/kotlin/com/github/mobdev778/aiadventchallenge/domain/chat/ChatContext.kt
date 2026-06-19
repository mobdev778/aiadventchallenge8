package com.github.mobdev778.aiadventchallenge.domain.chat

import com.github.mobdev778.aiadventchallenge.domain.chat.model.ChatMessage
import com.github.mobdev778.aiadventchallenge.domain.profile.model.Profile
import com.github.mobdev778.aiadventchallenge.domain.task.TaskContext
import java.util.UUID

data class ChatContext(
    val chatId: UUID,
    val profile: Profile,
    val taskContext: TaskContext?,
    val messages: List<ChatMessage>,
)