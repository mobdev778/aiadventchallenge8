package com.github.mobdev778.aiadventchallenge.domain.settings.model

data class AppSettings(
    val messageSelectionType: MessageSelectionType,
    val maxMessages: Int,
    val maxTokens: Int,
    val recursiveSummationMaxMessages: Int,
    val apiKey: String,
    val baseUrl: String,
    val baseModel: String,
)
