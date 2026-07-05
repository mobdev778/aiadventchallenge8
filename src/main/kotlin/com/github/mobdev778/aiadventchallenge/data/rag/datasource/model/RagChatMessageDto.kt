package com.github.mobdev778.aiadventchallenge.data.rag.datasource.model

import kotlinx.serialization.Serializable

@Serializable
data class RagChatMessageDto(
    val text: String,
    val english: String,
    val time: Long,
    val role: String,
)
