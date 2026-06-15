package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.composable.SettingsScreenContent
import org.koin.java.KoinJavaComponent.inject

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
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
            }
        }
    }
}

