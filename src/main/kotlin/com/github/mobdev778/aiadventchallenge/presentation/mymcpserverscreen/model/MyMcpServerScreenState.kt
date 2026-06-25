package com.github.mobdev778.aiadventchallenge.presentation.mymcpserverscreen.model

import androidx.compose.runtime.Immutable
import com.github.mobdev778.aiadventchallenge.domain.mymcpserver.model.MyMcpServer
import com.github.mobdev778.aiadventchallenge.domain.mymcpserver.model.MyMcpServerConfig

@Immutable
data class MyMcpServerScreenState(
    val savedConfig: MyMcpServerConfig = MyMcpServerConfig(
        launchAtStartup = false,
        port = 3000,
    ),
    val draftConfig: MyMcpServerConfig = MyMcpServerConfig(
        launchAtStartup = false,
        port = 3000,
    ),
    val portInput: String = "3000",
    val server: MyMcpServer = MyMcpServer(
        isRunning = false,
        url = "http://localhost:3000/mcp",
    ),
    val actionEnabled: Boolean = false,
)
