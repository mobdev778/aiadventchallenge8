package com.github.mobdev778.aiadventchallenge.presentation.addmcpserverscreen

sealed interface AddMcpServerScreenEvent {
    data object OnBackClick : AddMcpServerScreenEvent
    data class OnNameChange(val value: String) : AddMcpServerScreenEvent
    data class OnUrlChange(val value: String) : AddMcpServerScreenEvent
    data object OnAddClick : AddMcpServerScreenEvent
}
