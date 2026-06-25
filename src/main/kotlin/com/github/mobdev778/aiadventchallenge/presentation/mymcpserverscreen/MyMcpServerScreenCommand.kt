package com.github.mobdev778.aiadventchallenge.presentation.mymcpserverscreen

sealed interface MyMcpServerScreenCommand {
    data object Back : MyMcpServerScreenCommand
}
