package com.github.mobdev778.aiadventchallenge.presentation.mcpserverlistscreen

import com.github.mobdev778.aiadventchallenge.domain.mcpserver.McpServerInteractor
import com.github.mobdev778.aiadventchallenge.domain.mcpserver.model.McpServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import java.util.UUID

@Single
class McpServerListScreenStateHolder(
    private val mcpServerInteractor: McpServerInteractor,
    private val scope: CoroutineScope,
) {

    val servers: StateFlow<List<McpServer>> = mcpServerInteractor
        .allServersFlow
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope,
            SharingStarted.WhileSubscribed(SUBSCRIPTION_TIMEOUT),
            emptyList()
        )

    val commands = MutableSharedFlow<McpServerListScreenCommand>(
        extraBufferCapacity = 1,
    )

    fun onEvent(event: McpServerListScreenEvent) {
        when (event) {
            McpServerListScreenEvent.OnBackClick -> {
                commands.tryEmit(McpServerListScreenCommand.Back)
            }
            McpServerListScreenEvent.OnAddClick -> {
                commands.tryEmit(McpServerListScreenCommand.OpenAddServer)
            }
            is McpServerListScreenEvent.OnActiveChanged -> {
                updateActive(event.serverId, event.active)
            }
            is McpServerListScreenEvent.OnServerClick -> {
                commands.tryEmit(McpServerListScreenCommand.OpenServerInfo(event.serverId))
            }
            is McpServerListScreenEvent.OnDeleteClick -> {
                deleteServer(event.serverId)
            }
        }
    }

    private fun updateActive(serverId: UUID, active: Boolean) {
        scope.launch(Dispatchers.IO) {
            mcpServerInteractor.updateServerActive(serverId = serverId, active = active)
        }
    }

    private fun deleteServer(serverId: UUID) {
        scope.launch(Dispatchers.IO) {
            mcpServerInteractor.deleteServer(serverId = serverId)
        }
    }
}

const val SUBSCRIPTION_TIMEOUT = 5000L
