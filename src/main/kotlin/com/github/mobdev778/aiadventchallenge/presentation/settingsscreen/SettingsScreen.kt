package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.composable.SettingsScreenContent
import org.koin.java.KoinJavaComponent.inject

/**
 * Главная точка входа экрана настроек.
 *
 * Создаёт или получает [SettingsScreenStateHolder] через Koin-инъекцию, связывает
 * его состояние с UI-композицией и реагирует на команды, определённые в [SettingsScreenCommand],
 * перенаправляя навигационные действия обратно через переданные колбэки.
 *
 * @param onBack Колбэк, вызываемый при запросе возврата на предыдущий экран.
 * @param onOpenProfiles Колбэк для открытия экрана профилей.
 * @param onOpenRag Колбэк для открытия экрана RAG (Retrieval-Augmented Generation).
 * @param onOpenMcp Колбэк для открытия экрана конфигурации MCP.
 * @param onOpenMyMcp Колбэк для открытия экрана "Мой MCP".
 */
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenProfiles: () -> Unit,
    onOpenRag: () -> Unit,
    onOpenMcp: () -> Unit,
    onOpenMyMcp: () -> Unit,
) {
    val stateHolder = remember {
        inject<SettingsScreenStateHolder>(SettingsScreenStateHolder::class.java).value
    }
    val state = stateHolder.uiState.collectAsState().value

    SettingsScreenContent(
        state = state,
        onEvent = stateHolder::onEvent,
    )

    LaunchedEffect(stateHolder.commands) {
        stateHolder.commands.collect { command ->
            when (command) {
                SettingsScreenCommand.Back -> onBack()
                SettingsScreenCommand.OpenProfiles -> onOpenProfiles()
                SettingsScreenCommand.OpenRag -> onOpenRag()
                SettingsScreenCommand.OpenMcp -> onOpenMcp()
                SettingsScreenCommand.OpenMyMcp -> onOpenMyMcp()
            }
        }
    }
}
