package com.github.mobdev778.aiadventchallenge.presentation.mcpserverlistscreen

import com.github.mobdev778.aiadventchallenge.data.mcpserver.repository.McpServerRepository
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

@Single
class McpServerListScreenStateHolder(
    private val mcpServerRepository: McpServerRepository,
    private val scope: CoroutineScope,
) {

    val servers: StateFlow<List<McpServer>> = mcpServerRepository
        .observeServers()
        .flowOn(Dispatchers.IO)
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())

    val commands = MutableSharedFlow<McpServerListScreenCommand>(
        extraBufferCapacity = 1,
    )

    fun onEvent(event: McpServerListScreenEvent) {
        when (event) {
            McpServerListScreenEvent.OnBackClick -> commands.tryEmit(McpServerListScreenCommand.Back)
            McpServerListScreenEvent.OnAddClick -> commands.tryEmit(McpServerListScreenCommand.OpenAddServer)
            is McpServerListScreenEvent.OnActiveChanged -> updateActive(event.serverId, event.active)
            is McpServerListScreenEvent.OnServerClick -> {
                commands.tryEmit(McpServerListScreenCommand.OpenServerInfo(event.serverId))
            }
        }
    }

    private fun updateActive(serverId: java.util.UUID, active: Boolean) {
        scope.launch(Dispatchers.IO) {
            mcpServerRepository.updateServerActive(serverId = serverId, active = active)
        }
    }
}
