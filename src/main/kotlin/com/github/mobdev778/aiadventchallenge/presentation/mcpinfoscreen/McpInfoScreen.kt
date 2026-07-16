package com.github.mobdev778.aiadventchallenge.presentation.mcpinfoscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.github.mobdev778.aiadventchallenge.presentation.mcpinfoscreen.composable.McpInfoScreenContent
import org.koin.java.KoinJavaComponent.inject
import java.util.UUID

/**
 * Основная точка входа для экрана информации о MCP (Model Context Protocol).
 * [McpInfoScreen] отображает детали сервера MCP и реализует UI-логику,
 * связывая компонент [McpInfoScreenContent] с [McpInfoScreenStateHolder] через состояние и события.
 *
 * Функция отвечает за:
 * - Получение [McpInfoScreenStateHolder] через DI Koin для переданного идентификатора сервера.
 * - Сбор состояния ([state]) и передачу его в контент.
 * - Прослушивание команд ([McpInfoScreenCommand]) от [McpInfoScreenStateHolder],
 *   таких как [McpInfoScreenCommand.Back], которая инициирует навигацию назад.
 *
 * @param serverId уникальный идентификатор сервера MCP, данные которого необходимо отобразить.
 * @param onBack колбэк, вызываемый при необходимости вернуться на предыдущий экран (команда Back).
 */
@Composable
fun McpInfoScreen(
    serverId: UUID,
    onBack: () -> Unit,
) {
    val stateHolder = remember(serverId) {
        inject<McpInfoScreenStateHolder>(McpInfoScreenStateHolder::class.java).value.apply {
            setServerId(serverId)
        }
    }

    val state by stateHolder.state.collectAsState()

    McpInfoScreenContent(
        state = state,
        onEvent = stateHolder::onEvent,
    )

    LaunchedEffect(Unit) {
        stateHolder.commands.collect { command ->
            when (command) {
                McpInfoScreenCommand.Back -> onBack()
            }
        }
    }
}
