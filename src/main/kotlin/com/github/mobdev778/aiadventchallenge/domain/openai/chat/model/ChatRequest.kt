package com.github.mobdev778.aiadventchallenge.domain.openai.chat.model

data class ChatRequest(
    val model: String,
    val reasoningEffort: ReasoningEffort? = null,
    val messages: List<Message>,
    val temperature: Double = 0.7,
)
