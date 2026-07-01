package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.presentation.common.ScreenHeader
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.SettingsScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.model.SettingsScreenState
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Divider
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text

@Composable
fun SettingsScreenContent(
    state: SettingsScreenState,
    onEvent: (SettingsScreenEvent) -> Unit,
) {
    val neonHighlightedText = Color(0xFF04D9FF)

    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedButton(onClick = { onEvent(SettingsScreenEvent.OnBackClick) }) {
                Text("Back")
            }
        }
        ScreenHeader(
            modifier = Modifier.fillMaxWidth(),
            text = "Настройки",
        )

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

        Spacer(modifier = Modifier.size(16.dp))

        ContextManagementTypeTypeBlock(
            titleColor = neonHighlightedText,
            items = state.contextManagementTypes,
            maxMessages = state.draft.maxMessages,
            stickyFactsMaxMessages = state.draft.stickyFactsMaxMessages,
            onEvent = onEvent,
        )

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            orientation = Orientation.Horizontal,
        )

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Настройки LLM:",
            color = neonHighlightedText,
            textAlign = TextAlign.Center,
        )

        LabeledTextField(
            label = "apiKey",
            value = state.draft.apiKey,
            onValueChange = { onEvent(SettingsScreenEvent.OnApiKeyChanged(it)) },
            visualTransformation = PasswordVisualTransformation(),
        )

        LabeledTextField(
            label = "baseUrl",
            value = state.draft.baseUrl,
            onValueChange = { onEvent(SettingsScreenEvent.OnBaseUrlChanged(it)) },
        )

        LabeledTextField(
            label = "baseModel",
            value = state.draft.baseModel,
            onValueChange = { onEvent(SettingsScreenEvent.OnBaseModelChanged(it)) },
        )

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            orientation = Orientation.Horizontal,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (state.actionEnabled) {
                OutlinedButton(onClick = { onEvent(SettingsScreenEvent.OnResetClick) }) {
                    Text(
                        text = "Отменить",
                        color = neonHighlightedText,
                    )
                }
                OutlinedButton(onClick = { onEvent(SettingsScreenEvent.OnSaveClick) }) {
                    Text(
                        text = "Сохранить",
                        color = neonHighlightedText,
                    )
                }
            }
        }
    }
}