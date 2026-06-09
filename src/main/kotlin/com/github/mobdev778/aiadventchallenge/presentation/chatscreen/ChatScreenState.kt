package com.github.mobdev778.aiadventchallenge.presentation.chatscreen

import com.github.mobdev778.aiadventchallenge.domain.chathistory.ChatMessage

data class ChatScreenState(
    val messages: List<ChatMessage>,
    val inputText: String,
)