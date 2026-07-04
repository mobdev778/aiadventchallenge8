package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model

import com.github.mobdev778.aiadventchallenge.domain.chat.model.Chat
import com.github.mobdev778.aiadventchallenge.domain.task.TaskContext

data class ChatScreenState(
    val chat: Chat,
    val messages: List<ChatUiMessage>,
    val contextManagementState: ContextManagementState,
    val inputText: String,
    val taskContext: TaskContext?,
    val autoPlay: Boolean,
)
