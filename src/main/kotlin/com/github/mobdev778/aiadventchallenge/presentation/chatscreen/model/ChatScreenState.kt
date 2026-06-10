package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model

data class ChatScreenState(
    val messages: List<ChatUiMessage>,
    val tokenLimitState: TokenLimitState,
    val inputText: String,
)