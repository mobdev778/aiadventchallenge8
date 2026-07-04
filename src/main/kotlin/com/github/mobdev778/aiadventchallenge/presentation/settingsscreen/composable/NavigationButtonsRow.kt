package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.SettingsScreenEvent
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text

@Composable
fun NavigationButtonsRow(onEvent: (SettingsScreenEvent) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            DefaultButton(onClick = { onEvent(SettingsScreenEvent.OnOpenProfilesClick) }) {
                Text("Профили")
            }

            Spacer(modifier = Modifier.padding(horizontal = 4.dp))

            DefaultButton(onClick = { onEvent(SettingsScreenEvent.OnOpenRagClick) }) {
                Text("RAG")
            }

            Spacer(modifier = Modifier.padding(horizontal = 4.dp))

            DefaultButton(onClick = { onEvent(SettingsScreenEvent.OnOpenMcpClick) }) {
                Text("MCP")
            }

            Spacer(modifier = Modifier.padding(horizontal = 4.dp))

            DefaultButton(onClick = { onEvent(SettingsScreenEvent.OnOpenMyMcpClick) }) {
                Text("MyMCP")
            }
        }
    }
}
