package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.SettingsScreenEvent
import org.jetbrains.jewel.ui.component.Text

@Composable
fun LlmSettingsBlock(
    apiKey: String,
    baseUrl: String,
    baseModel: String,
    titleColor: Color,
    onEvent: (SettingsScreenEvent) -> Unit,
) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = "Настройки LLM:",
        color = titleColor,
        textAlign = TextAlign.Center,
    )

    LabeledTextField(
        label = "apiKey",
        value = apiKey,
        onValueChange = { onEvent(SettingsScreenEvent.OnApiKeyChanged(it)) },
        visualTransformation = PasswordVisualTransformation(),
    )

    LabeledTextField(
        label = "baseUrl",
        value = baseUrl,
        onValueChange = { onEvent(SettingsScreenEvent.OnBaseUrlChanged(it)) },
    )

    LabeledTextField(
        label = "baseModel",
        value = baseModel,
        onValueChange = { onEvent(SettingsScreenEvent.OnBaseModelChanged(it)) },
    )
}
