package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model

import com.github.mobdev778.aiadventchallenge.domain.chat.model.Chat

data class ChatScreenState(
    val chat: Chat,
    val messages: List<ChatUiMessage>,
    val contextManagementState: ContextManagementState,
    val inputText: String,
)