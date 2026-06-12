package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model

sealed interface TokenLimitState {
    data object FullHistory : TokenLimitState
    data class LimitMessages(val messages: Int, val maxMessages: Int) : TokenLimitState
    data class LimitTokens(val tokens: Int, val maxTokens: Int) : TokenLimitState
    data class RecursiveSummation(val messages: Int, val maxMessages: Int): TokenLimitState
}