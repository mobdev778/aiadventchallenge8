package com.github.mobdev778.aiadventchallenge.presentation.chatscreen

sealed interface ChatScreenCommand {
    data object Back : ChatScreenCommand
    data object OpenSettings : ChatScreenCommand
}