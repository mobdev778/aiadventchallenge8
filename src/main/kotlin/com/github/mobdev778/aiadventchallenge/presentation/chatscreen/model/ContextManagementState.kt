package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model

sealed interface ContextManagementState {
    data object None : ContextManagementState
    data class SlidingWindow(val messages: Int, val maxMessages: Int) : ContextManagementState
    data class StickFacts(val messages: Int, val maxMessages: Int): ContextManagementState
    data object Branching : ContextManagementState
}