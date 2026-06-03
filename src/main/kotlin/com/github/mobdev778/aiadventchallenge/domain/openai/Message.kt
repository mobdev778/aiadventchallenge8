package com.github.mobdev778.aiadventchallenge.domain.openai

data class Message(
    val role: Role,
    val content: String?,
)
