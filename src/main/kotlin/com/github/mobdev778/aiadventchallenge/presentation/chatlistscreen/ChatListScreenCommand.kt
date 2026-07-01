package com.github.mobdev778.aiadventchallenge.presentation.chatlistscreen

import java.util.UUID

sealed interface ChatListScreenCommand {
    data class OpenChat(val chatId: UUID) : ChatListScreenCommand
    data object OpenSettings : ChatListScreenCommand
}
