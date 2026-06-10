package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.domain.messageselection.MessageSelectionType
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.SettingsScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.SettingsScreenState
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.Divider
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text

@Composable
fun SettingsScreenContent(
    state: SettingsScreenState,
    onEvent: (SettingsScreenEvent) -> Unit,
) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Settings")

        LabeledDropdown(
            label = "Лимит контекстного окна",
            selectedText = state.draft.messageSelectionType.name,
            items = MessageSelectionType.entries.map {
                when (it) {
                    MessageSelectionType.FullHistory -> "- Не задан -"
                    MessageSelectionType.MessageLimit -> "Лимит сообщений"
                    MessageSelectionType.TokenLimit -> "Лимит токенов"
                }
                it.name
            },
            onItemSelected = { index ->
                onEvent(
                    SettingsScreenEvent.OnMessageSelectionTypeChanged(
                        MessageSelectionType.entries[index]
                    )
                )
            },
        )

        if (state.draft.messageSelectionType == MessageSelectionType.MessageLimit) {
            LabeledTextField(
                label = "Max сообщений",
                value = state.draft.maxMessages,
                onValueChange = { onEvent(SettingsScreenEvent.OnLastNMessagesChanged(it)) },
            )
        }

        if (state.draft.messageSelectionType == MessageSelectionType.TokenLimit) {
            LabeledTextField(
                label = "Max токенов",
                value = state.draft.maxTokens,
                onValueChange = { onEvent(SettingsScreenEvent.OnMaxTokensChanged(it)) },
            )
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            orientation = Orientation.Horizontal,
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedButton(onClick = { onEvent(SettingsScreenEvent.OnResetClick) }) {
                Text("Reset")
            }
            OutlinedButton(onClick = { onEvent(SettingsScreenEvent.OnSaveClick) }) {
                Text("Save")
            }
        }
    }
}