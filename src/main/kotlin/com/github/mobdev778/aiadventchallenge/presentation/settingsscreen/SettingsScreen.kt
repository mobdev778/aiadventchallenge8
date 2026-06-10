package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.composable.SettingsScreenContent

@Composable
fun SettingsScreen(
    stateHolder: SettingsScreenStateHolder,
) {
    val state = stateHolder.uiState.collectAsState().value

    SettingsScreenContent(
        state = state,
        onEvent = stateHolder::onEvent,
    )
}

