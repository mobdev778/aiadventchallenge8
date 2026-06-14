package com.github.mobdev778.aiadventchallenge.domain.settings.model

data class AppSettings(
    val contextManagementType: ContextManagementType,
    val maxMessages: Int,
    val maxTokens: Int,
    val recursiveSummationMaxMessages: Int,
    val stickyFactsMaxMessages: Int,
    val apiKey: String,
    val baseUrl: String,
    val baseModel: String,
)
