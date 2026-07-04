package com.github.mobdev778.aiadventchallenge.domain.mymcpserver.model

data class MyMcpServerState(
    val name: String,
    val description: String,
    val isRunning: Boolean,
    val url: String,
    val launchAtStartup: Boolean,
)
