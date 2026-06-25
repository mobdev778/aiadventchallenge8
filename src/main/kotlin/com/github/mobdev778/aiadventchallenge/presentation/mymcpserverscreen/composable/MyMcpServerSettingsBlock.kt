package com.github.mobdev778.aiadventchallenge.presentation.mymcpserverscreen.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.github.mobdev778.aiadventchallenge.presentation.mymcpserverscreen.MyMcpServerScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.mymcpserverscreen.model.MyMcpServerScreenState
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.composable.LabeledTextField
import org.jetbrains.jewel.ui.component.CheckboxRow
import org.jetbrains.jewel.ui.component.Text

@Composable
fun MyMcpServerSettingsBlock(state: MyMcpServerScreenState, onEvent: (MyMcpServerScreenEvent) -> Unit) {
    val neonHighlightedText = Color(0xFF04D9FF)

    Text(
        modifier = Modifier.fillMaxWidth(),
        text = "Настройки локального MCP-сервера",
        color = neonHighlightedText,
        textAlign = TextAlign.Center,
    )

    CheckboxRow(
        checked = state.draftConfig.launchAtStartup,
        onCheckedChange = { onEvent(MyMcpServerScreenEvent.OnLaunchAtStartupChanged(it)) },
    ) {
        Text("Запускать на старте")
    }

    LabeledTextField(
        label = "Порт: ",
        value = state.portInput,
        onValueChange = { onEvent(MyMcpServerScreenEvent.OnPortChanged(it)) },
    )
}