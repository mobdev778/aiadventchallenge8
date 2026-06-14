package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.composable.SettingsScreenContent

@Composable
fun SettingsScreen(
    stateHolder: SettingsScreenStateHolder,
    onBack: () -> Unit,
) {
    val state = stateHolder.uiState.collectAsState().value

    SettingsScreenContent(
        state = state,
        onEvent = stateHolder::onEvent,
    )

    LaunchedEffect(stateHolder.commands) {
        stateHolder.commands.collect { command ->
            when (command) {
                SettingsScreenCommand.Back -> onBack()
            }
        }
    }
}

