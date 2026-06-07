package com.github.mobdev778.aiadventchallenge.domain.openai.chat.model

data class Choice(
    val message: Message,
    val finishReason: FinishReason,
)
