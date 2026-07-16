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

/**
 * Управляет состоянием и командами экрана добавления MCP-сервера.
 *
 * Этот класс является синглтоном, регистрируемым в Koin, и отвечает за обработку
 * пользовательских событий [AddMcpServerScreenEvent], обновление [AddMcpServerScreenState]
 * и выдачу навигационных / управляющих команд через [commands].
 *
 * Взаимодействует с [McpServerRepository] для сохранения нового сервера при добавлении.
 *
 * @param mcpServerRepository репозиторий для работы с моделями MCP-серверов.
 * @param scope coroutine‑scope, в рамках которого выполняются асинхронные операции
 *              (например, ввод/вывод при создании сервера).
 */
@Single
class AddMcpServerScreenStateHolder(
    private val mcpServerRepository: McpServerRepository,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(AddMcpServerScreenState())
    /**
     * Текущее состояние экрана (введённые имя и URL).
     * Доступно для наблюдения в виде [StateFlow].
     */
    val state: StateFlow<AddMcpServerScreenState> = _state.asStateFlow()

    /**
     * Поток команд, генерируемых в ответ на действия пользователя.
     * Используется для навигации (например, возврат назад) и других side‑эффектов.
     * Имеет дополнительный буфер на одну команду, чтобы не блокировать отправку.
     */
    val commands = MutableSharedFlow<AddMcpServerScreenCommand>(
        extraBufferCapacity = 1,
    )

    /**
     * Обрабатывает входящее UI‑событие.
     *
     * @param event событие от экрана добавления MCP-сервера.
     */
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
                isLocal = false,
            )
            mcpServerRepository.createServer(server)
            commands.tryEmit(AddMcpServerScreenCommand.Back)
        }
    }
}
