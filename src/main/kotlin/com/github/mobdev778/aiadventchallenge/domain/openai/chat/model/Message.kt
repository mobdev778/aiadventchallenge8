package com.github.mobdev778.aiadventchallenge.domain.openai.chat.model

data class Message(
    val role: Role,
    val content: String?,
)
