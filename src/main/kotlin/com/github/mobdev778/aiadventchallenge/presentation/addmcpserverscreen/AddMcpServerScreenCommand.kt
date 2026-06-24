package com.github.mobdev778.aiadventchallenge.presentation.addmcpserverscreen

sealed interface AddMcpServerScreenCommand {
    data object Back : AddMcpServerScreenCommand
}
