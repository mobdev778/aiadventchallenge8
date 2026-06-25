package com.github.mobdev778.aiadventchallenge.presentation.logsscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import com.github.mobdev778.aiadventchallenge.presentation.logsscreen.composable.LogsFileScreenContent
import org.koin.java.KoinJavaComponent.inject

@Composable
fun LogsFileScreen() {
    val stateHolder = remember {
        inject<LogsFileScreenStateHolder>(LogsFileScreenStateHolder::class.java).value
    }
    val state = stateHolder.state.collectAsState().value

    LaunchedEffect(Unit) {
        stateHolder.startObserving()
    }

    LogsFileScreenContent(state = state)
}
