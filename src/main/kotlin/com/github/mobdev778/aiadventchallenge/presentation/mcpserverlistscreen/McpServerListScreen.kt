package com.github.mobdev778.aiadventchallenge.presentation.mcpserverlistscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.github.mobdev778.aiadventchallenge.presentation.mcpserverlistscreen.composable.McpServerListScreenContent
import org.koin.java.KoinJavaComponent.inject

@Composable
fun McpServerListScreen(
    onBack: () -> Unit,
    onOpenAddServer: () -> Unit,
    onOpenServerInfo: (java.util.UUID) -> Unit,
) {
    val stateHolder = remember {
        inject<McpServerListScreenStateHolder>(McpServerListScreenStateHolder::class.java).value
    }

    val servers by stateHolder.servers.collectAsState()

    McpServerListScreenContent(
        servers = servers,
        onEvent = stateHolder::onEvent,
    )

    LaunchedEffect(Unit) {
        stateHolder.commands.collect { command ->
            when (command) {
                McpServerListScreenCommand.Back -> onBack()
                McpServerListScreenCommand.OpenAddServer -> onOpenAddServer()
                is McpServerListScreenCommand.OpenServerInfo -> onOpenServerInfo(command.serverId)
            }
        }
    }
}
