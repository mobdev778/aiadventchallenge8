package com.github.mobdev778.aiadventchallenge.presentation.mcpinfoscreen

import com.github.mobdev778.aiadventchallenge.domain.mcpserver.McpServerInteractor
import com.github.mobdev778.aiadventchallenge.domain.mcpserver.McpToolsChecker
import com.github.mobdev778.aiadventchallenge.presentation.mcpinfoscreen.model.McpInfoScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import java.util.UUID

/**
 * State-холдер экрана информации MCP-сервера, отвечающий за управление
 * состоянием экрана, загрузку данных о сервере и выполнение проверки инструментов.
 *
 * Является центральным компонентом UI-слоя экрана, реализуя паттерн unidirectional
 * data flow (UDF). Получает данные через [McpServerInteractor], проверяет доступность
 * инструментов с помощью [McpToolsChecker] и предоставляет реактивные потоки состояния
 * ([state]) и команд ([commands]) для View-слоя. Обрабатывает события
 * [McpInfoScreenEvent], преобразуя их в соответствующие действия и команды.
 *
 * @param mcpServerInteractor интерактор для получения списка MCP-серверов и доступа к их данным
 * @param mcpToolsChecker компонент для загрузки инструментов конкретного сервера по URL
 * @param scope корутинный скоуп, в котором выполняются асинхронные операции
 *              (обычно привязанный к жизненному циклу компонента)
 */
@Single
class McpInfoScreenStateHolder(
    private val mcpServerInteractor: McpServerInteractor,
    private val mcpToolsChecker: McpToolsChecker,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(McpInfoScreenState())
    /**
     * Поток состояния экрана информации MCP. Содержит текущие данные сервера,
     * флаг загрузки и текстовое представление инструментов.
     */
    val state: StateFlow<McpInfoScreenState> = _state.asStateFlow()

    /**
     * Поток однократных команд для навигации или других одноразовых действий UI.
     * Связывается с [McpInfoScreenCommand], обеспечивая обработку в `when`-выражениях.
     */
    val commands = MutableSharedFlow<McpInfoScreenCommand>(
        extraBufferCapacity = 1,
    )

    /**
     * Устанавливает идентификатор сервера, для которого необходимо отобразить информацию.
     * Загружает объект [McpServer] из общего потока всех серверов, используя [McpServerInteractor],
     * и обновляет состояние экрана. Если идентификатор совпадает с текущим, метод ничего не делает.
     *
     * @param serverId уникальный идентификатор сервера MCP
     */
    fun setServerId(serverId: UUID) {
        if (state.value.server?.id == serverId) return

        scope.launch(Dispatchers.IO) {
            val server = mcpServerInteractor.allServersFlow.firstOrNull()?.firstOrNull { it.id == serverId }

            _state.update {
                it.copy(
                    server = server,
                    isLoading = false,
                    toolsText = "",
                )
            }
        }
    }

    /**
     * Обрабатывает пользовательские события экрана информации MCP.
     *
     * При событии [McpInfoScreenEvent.OnBackClick] генерирует команду [McpInfoScreenCommand.Back].
     * При событии [McpInfoScreenEvent.OnCheckClick] запускает проверку инструментов сервера.
     *
     * @param event событие, инициированное пользователем
     */
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
                val tools = mcpToolsChecker.loadTools(server.url).map { it.name }
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
