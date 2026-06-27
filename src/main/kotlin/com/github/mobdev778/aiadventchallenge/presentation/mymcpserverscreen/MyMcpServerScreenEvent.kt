package com.github.mobdev778.aiadventchallenge.presentation.mymcpserverscreen

sealed interface MyMcpServerScreenEvent {
    data object OnBackClick : MyMcpServerScreenEvent
    data class OnStartClick(val name: String) : MyMcpServerScreenEvent
    data class OnStopClick(val name: String) : MyMcpServerScreenEvent
}
