package com.github.mobdev778.aiadventchallenge.domain.chathistory.model

data class ChatMessage(
    val id: Long,
    val text: String,
    val author: ChatAuthor,
    val tokens: Int,
)