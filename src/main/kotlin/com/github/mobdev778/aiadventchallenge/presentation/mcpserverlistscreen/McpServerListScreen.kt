package com.github.mobdev778.aiadventchallenge.presentation.mcpserverlistscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.github.mobdev778.aiadventchallenge.presentation.mcpserverlistscreen.composable.McpServerListScreenContent
import org.koin.java.KoinJavaComponent.inject

/**
 * Экран со списком MCP-серверов.
 *
 * Отображает перечень доступных серверов MCP и позволяет пользователю переходить
 * на экран добавления нового сервера, просматривать информацию о конкретном сервере
 * или возвращаться на предыдущий экран.
 *
 * Использует [McpServerListScreenStateHolder] в качестве источника состояния и
 * [McpServerListScreenContent] для отображения содержимого. Команды, генерируемые
 * UI-компонентами (через обратный вызов `onEvent`), передаются в `stateHolder`,
 * а затем через канал команд (`commands`) преобразуются в навигационные вызовы,
 * переданные в параметры.
 *
 * @param onBack Коллбэк для возврата на предыдущий экран.
 * @param onOpenAddServer Коллбэк для перехода на экран добавления нового MCP-сервера.
 * @param onOpenServerInfo Коллбэк для открытия экрана с подробной информацией о сервере.
 *                          Принимает уникальный идентификатор сервера [java.util.UUID].
 */
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
