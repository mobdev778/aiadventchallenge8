package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model

import com.github.mobdev778.aiadventchallenge.domain.chathistory.model.ChatMessage

data class ChatUiMessage(
    val message: ChatMessage,
    val insideWindow: Boolean, // признак попадания сообщения "в окно"
)