package com.github.mobdev778.aiadventchallenge.domain.openai.chat.model

data class ChatResponse(
    val choices: List<Choice>,
    val usage: Usage?,
)
