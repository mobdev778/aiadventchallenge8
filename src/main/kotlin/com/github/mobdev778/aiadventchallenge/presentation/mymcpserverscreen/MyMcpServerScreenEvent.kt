package com.github.mobdev778.aiadventchallenge.presentation.mymcpserverscreen

sealed interface MyMcpServerScreenEvent {
    data object OnBackClick : MyMcpServerScreenEvent
    data class OnLaunchAtStartupChanged(val value: Boolean) : MyMcpServerScreenEvent
    data class OnPortChanged(val value: String) : MyMcpServerScreenEvent
    data object OnSaveClick : MyMcpServerScreenEvent
    data object OnResetClick : MyMcpServerScreenEvent
    data object OnStartStopClick : MyMcpServerScreenEvent
}
