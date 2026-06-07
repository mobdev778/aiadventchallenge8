package com.github.mobdev778.aiadventchallenge.domain.openai.chat.model

data class Usage(
    val promptTokens: Int,
    val completionTokens: Int,
    val totalTokens: Int
)