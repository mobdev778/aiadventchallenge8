package com.github.mobdev778.aiadventchallenge.presentation.rag.ragconfigscreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.presentation.rag.ragconfigscreen.composable.RagConfigEditor
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text
import org.koin.java.KoinJavaComponent.inject

@Composable
fun RagConfigScreen(
    onBack: () -> Unit,
) {
    val stateHolder = remember {
        inject<RagConfigScreenStateHolder>(RagConfigScreenStateHolder::class.java).value
    }

    val ragConfig by stateHolder.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DefaultButton(onClick = { stateHolder.onEvent(RagConfigScreenEvent.OnBackClick) }) {
                Text("Назад")
            }
        }

        RagConfigEditor(
            ragConfig = ragConfig,
            onEvent = stateHolder::onEvent
        )
    }

    LaunchedEffect(Unit) {
        stateHolder.commands.collect { command ->
            when (command) {
                RagConfigScreenCommand.Back -> onBack()
            }
        }
    }
}
