package com.github.mobdev778.aiadventchallenge.presentation.chatscreen

import java.util.UUID

sealed interface ChatScreenCommand {
    data object Back : ChatScreenCommand
    data object OpenSettings : ChatScreenCommand
    data class OpenTaskContext(val taskContextId: UUID, val chatId: UUID) : ChatScreenCommand
}
