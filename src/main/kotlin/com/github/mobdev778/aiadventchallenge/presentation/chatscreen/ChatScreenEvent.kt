package com.github.mobdev778.aiadventchallenge.presentation.chatscreen

sealed interface ChatScreenEvent {
    data object OnClearAllMessagesClick : ChatScreenEvent
    data class OnInputTextChanged(val text: String) : ChatScreenEvent
    data object OnSendMessageClick : ChatScreenEvent
}