package com.github.mobdev778.aiadventchallenge.presentation.addmcpserverscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.presentation.addmcpserverscreen.AddMcpServerScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.addmcpserverscreen.model.AddMcpServerScreenState
import com.github.mobdev778.aiadventchallenge.presentation.common.ScreenHeader
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.composable.LabeledTextField
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text

@Composable
fun AddMcpServerScreenContent(
    state: AddMcpServerScreenState,
    onEvent: (AddMcpServerScreenEvent) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DefaultButton(onClick = { onEvent(AddMcpServerScreenEvent.OnBackClick) }) {
                Text("Назад")
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        ScreenHeader(
            modifier = Modifier.fillMaxWidth(),
            text = "Добавление MCP сервера",
        )

        Spacer(modifier = Modifier.size(16.dp))

        Text("Название")
        LabeledTextField(
            label = "",
            value = state.name,
            onValueChange = { onEvent(AddMcpServerScreenEvent.OnNameChange(it)) },
        )

        Spacer(modifier = Modifier.size(12.dp))

        Text("URL")
        LabeledTextField(
            label = "",
            value = state.url,
            onValueChange = { onEvent(AddMcpServerScreenEvent.OnUrlChange(it)) },
        )

        Spacer(modifier = Modifier.size(16.dp))

        DefaultButton(onClick = { onEvent(AddMcpServerScreenEvent.OnAddClick) }) {
            Text("Добавить")
        }
    }
}
