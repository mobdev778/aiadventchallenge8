package com.github.mobdev778.aiadventchallenge.presentation.addmcpserverscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.github.mobdev778.aiadventchallenge.presentation.addmcpserverscreen.composable.AddMcpServerScreenContent
import org.koin.java.KoinJavaComponent.inject

@Composable
fun AddMcpServerScreen(
    onBack: () -> Unit,
) {
    val stateHolder = remember {
        inject<AddMcpServerScreenStateHolder>(AddMcpServerScreenStateHolder::class.java).value
    }

    val state by stateHolder.state.collectAsState()

    AddMcpServerScreenContent(
        state = state,
        onEvent = stateHolder::onEvent,
    )

    LaunchedEffect(Unit) {
        stateHolder.commands.collect { command ->
            when (command) {
                AddMcpServerScreenCommand.Back -> onBack()
            }
        }
    }
}
