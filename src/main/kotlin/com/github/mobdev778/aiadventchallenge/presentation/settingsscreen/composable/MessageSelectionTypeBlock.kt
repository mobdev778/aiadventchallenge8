package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.github.mobdev778.aiadventchallenge.domain.settings.model.MessageSelectionType
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.SettingsScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.model.MessageSelectionTypeUi

@Composable
fun MessageSelectionTypeBlock(
    items: List<MessageSelectionTypeUi>,
    titleColor: Color,
    maxMessages: Int,
    maxTokens: Int,
    recursiveSummationMaxMessages: Int,
    onEvent: (SettingsScreenEvent) -> Unit,
) {
    val selectedItem = items.first { it.selected }

    LabeledDropdown(
        label = "Способ ограничения контекстного окна:",
        labelColor = titleColor,
        selectedText = selectedItem.label,
        items = items.map { it.label },
        onItemSelected = { index ->
            onEvent(
                SettingsScreenEvent.OnMessageSelectionTypeChanged(
                    items[index].type
                )
            )
        },
    )

    if (selectedItem.type == MessageSelectionType.MessageLimit) {
        LabeledTextField(
            label = "Max сообщений",
            value = maxMessages.toString(),
            onValueChange = { onEvent(SettingsScreenEvent.OnMaxMessagesChanged(it)) },
        )
    }

    if (selectedItem.type == MessageSelectionType.TokenLimit) {
        LabeledTextField(
            label = "Max токенов",
            value = maxTokens.toString(),
            onValueChange = { onEvent(SettingsScreenEvent.OnMaxTokensChanged(it)) },
        )
    }

    if (selectedItem.type == MessageSelectionType.RecursiveSummation) {
        LabeledTextField(
            label = "Max сообщений",
            value = recursiveSummationMaxMessages.toString(),
            onValueChange = { onEvent(SettingsScreenEvent.OnRecursiveSummationMaxMessagesChanged(it)) },
        )
    }
}