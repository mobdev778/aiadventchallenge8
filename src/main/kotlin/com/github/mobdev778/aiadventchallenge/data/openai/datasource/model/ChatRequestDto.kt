package com.github.mobdev778.aiadventchallenge.data.openai.datasource.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatRequestDto(
    val model: String,
    val messages: List<MessageDto>,
    val temperature: Double,
)