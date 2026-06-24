package com.github.mobdev778.aiadventchallenge.presentation.mcpinfoscreen

import com.github.mobdev778.aiadventchallenge.data.mcpserver.repository.McpServerRepository
import com.github.mobdev778.aiadventchallenge.domain.mcpserver.McpToolsChecker
import com.github.mobdev778.aiadventchallenge.presentation.mcpinfoscreen.model.McpInfoScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import java.util.UUID

@Single
class McpInfoScreenStateHolder(
    private val mcpServerRepository: McpServerRepository,
    private val mcpToolsChecker: McpToolsChecker,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(McpInfoScreenState())
    val state: StateFlow<McpInfoScreenState> = _state.asStateFlow()

    val commands = MutableSharedFlow<McpInfoScreenCommand>(
        extraBufferCapacity = 1,
    )

    fun setServerId(serverId: UUID) {
        if (state.value.server?.id == serverId) return

        scope.launch(Dispatchers.IO) {
            val server = mcpServerRepository.getServer(serverId)

            _state.update {
                it.copy(
                    server = server,
                    isLoading = false,
                    toolsText = "",
                )
            }
        }
    }

    fun onEvent(event: McpInfoScreenEvent) {
        when (event) {
            McpInfoScreenEvent.OnBackClick -> commands.tryEmit(McpInfoScreenCommand.Back)
            McpInfoScreenEvent.OnCheckClick -> checkTools()
        }
    }

    private fun checkTools() {
        val server = state.value.server ?: return

        scope.launch(Dispatchers.IO) {
            _state.update {
                it.copy(
                    isLoading = true,
                    toolsText = "Проверка подключения...",
                )
            }

            val toolsText = runCatching {
                val tools = mcpToolsChecker.loadTools(server.url)
                if (tools.isEmpty()) {
                    "Инструменты не найдены"
                } else {
                    tools.joinToString(separator = "\n")
                }
            }.getOrElse { error ->
                error.printStackTrace()
                error.message ?: error.toString()
            }

            _state.update {
                it.copy(
                    isLoading = false,
                    toolsText = toolsText,
                )
            }
        }
    }
}
