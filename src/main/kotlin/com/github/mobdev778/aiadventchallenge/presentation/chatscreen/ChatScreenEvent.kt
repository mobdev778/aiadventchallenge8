package com.github.mobdev778.aiadventchallenge.presentation.chatscreen

import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model.ChatUiMessage

sealed interface ChatScreenEvent {
    data object OnClearAllMessagesClick : ChatScreenEvent
    data class OnMessageClicked(val message: ChatUiMessage) : ChatScreenEvent
    data class OnInputTextChanged(val text: String) : ChatScreenEvent
    data object OnSendMessageClick : ChatScreenEvent
}