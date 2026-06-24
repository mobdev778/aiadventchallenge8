package com.github.mobdev778.aiadventchallenge.presentation.addmcpserverscreen

import com.github.mobdev778.aiadventchallenge.data.mcpserver.repository.McpServerRepository
import com.github.mobdev778.aiadventchallenge.domain.mcpserver.model.McpServer
import com.github.mobdev778.aiadventchallenge.presentation.addmcpserverscreen.model.AddMcpServerScreenState
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
class AddMcpServerScreenStateHolder(
    private val mcpServerRepository: McpServerRepository,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(AddMcpServerScreenState())
    val state: StateFlow<AddMcpServerScreenState> = _state.asStateFlow()

    val commands = MutableSharedFlow<AddMcpServerScreenCommand>(
        extraBufferCapacity = 1,
    )

    fun onEvent(event: AddMcpServerScreenEvent) {
        when (event) {
            AddMcpServerScreenEvent.OnBackClick -> commands.tryEmit(AddMcpServerScreenCommand.Back)
            is AddMcpServerScreenEvent.OnNameChange -> _state.update { it.copy(name = event.value) }
            is AddMcpServerScreenEvent.OnUrlChange -> _state.update { it.copy(url = event.value) }
            AddMcpServerScreenEvent.OnAddClick -> addServer()
        }
    }

    private fun addServer() {
        val snapshot = state.value
        val name = snapshot.name.trim()
        val url = snapshot.url.trim()
        if (name.isEmpty() || url.isEmpty()) return

        scope.launch(Dispatchers.IO) {
            val server = McpServer(
                id = UUID.randomUUID(),
                active = false,
                name = name,
                url = url,
            )
            mcpServerRepository.createServer(server)
            commands.tryEmit(AddMcpServerScreenCommand.Back)
        }
    }
}
