package com.github.mobdev778.aiadventchallenge.presentation.chatscreen

/**
 * Message model for UI.
 */
data class ChatMessageUi(
    val id: Long,
    val text: String,
    val author: ChatAuthor,
)