package com.github.mobdev778.aiadventchallenge.presentation.chatscreen

/**
 * UI state for one-off chat imitation.
 */
data class ChatScreenUiState(
    val messages: List<ChatMessageUi> = emptyList(),
    val inputText: String = "",
)

