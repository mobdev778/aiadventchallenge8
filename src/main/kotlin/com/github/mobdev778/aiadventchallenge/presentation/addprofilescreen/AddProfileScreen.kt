package com.github.mobdev778.aiadventchallenge.presentation.addprofilescreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.github.mobdev778.aiadventchallenge.presentation.addprofilescreen.composable.AddProfileScreenContent
import org.koin.java.KoinJavaComponent.inject

@Composable
fun AddProfileScreen(
    onBack: () -> Unit,
) {
    val stateHolder = remember {
        inject<AddProfileScreenStateHolder>(AddProfileScreenStateHolder::class.java).value
    }

    val state by stateHolder.state.collectAsState()

    AddProfileScreenContent(
        state = state,
        onEvent = stateHolder::onEvent,
    )

    LaunchedEffect(Unit) {
        stateHolder.commands.collect { command ->
            when (command) {
                AddProfileScreenCommand.Back -> onBack()
            }
        }
    }
}
