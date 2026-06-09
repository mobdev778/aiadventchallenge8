package com.github.mobdev778.aiadventchallenge.domain.chathistory

data class ChatMessage(
    val id: Long,
    val text: String,
    val author: ChatAuthor,
)