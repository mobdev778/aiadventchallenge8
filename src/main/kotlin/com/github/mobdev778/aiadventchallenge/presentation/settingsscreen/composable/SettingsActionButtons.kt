package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.SettingsScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.model.SettingsScreenState
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text

@Suppress("MagicNumber")
private val NeonHighlightedTextColor = Color(0xFF04D9FF)

@Composable
fun SettingsActionButtons(
    state: SettingsScreenState,
    onEvent: (SettingsScreenEvent) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (state.actionEnabled) {
            OutlinedButton(onClick = { onEvent(SettingsScreenEvent.OnResetClick) }) {
                Text(
                    text = "Отменить",
                    color = NeonHighlightedTextColor,
                )
            }
            OutlinedButton(onClick = { onEvent(SettingsScreenEvent.OnSaveClick) }) {
                Text(
                    text = "Сохранить",
                    color = NeonHighlightedTextColor,
                )
            }
        }
    }
}
