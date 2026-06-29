package com.github.mobdev778.aiadventchallenge.presentation.chatlistscreen

import java.util.UUID

sealed interface ChatListScreenEvent {
    data object OnOpenSettingsClick : ChatListScreenEvent
    data object OnOpenProfilesClick : ChatListScreenEvent
    data object OnOpenRagClick : ChatListScreenEvent
    data object OnOpenMcpClick : ChatListScreenEvent
    data object OnOpenMyMcpClick : ChatListScreenEvent
    data class OnOpenChatClick(val chatId: UUID) : ChatListScreenEvent
    data class OnCreateChatClick(val name: String) : ChatListScreenEvent
    data class OnDeleteChatClick(val chatId: UUID) : ChatListScreenEvent
}
