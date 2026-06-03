package com.github.mobdev778.aiadventchallenge.data.openai.datasource.model

import kotlinx.serialization.Serializable

@Serializable
data class MessageDto(
    val role: String,
    val content: String
)