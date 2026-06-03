package com.github.mobdev778.aiadventchallenge.domain.openai

data class Choice(
    val message: Message,
    val finishReason: FinishReason,
)
