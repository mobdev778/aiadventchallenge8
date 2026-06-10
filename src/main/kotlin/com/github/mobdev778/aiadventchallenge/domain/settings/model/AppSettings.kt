package com.github.mobdev778.aiadventchallenge.domain.settings.model

import com.github.mobdev778.aiadventchallenge.domain.messageselection.MessageSelectionType

data class AppSettings(
    val messageSelectionType: MessageSelectionType,
    val maxMessages: Int,
    val maxTokens: Int,
    val apiKey: String,
    val baseUrl: String,
    val baseModel: String,
)
