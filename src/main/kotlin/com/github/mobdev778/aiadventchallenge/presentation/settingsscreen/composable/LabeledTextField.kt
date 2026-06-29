package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.ui.component.Text

@Composable
fun LabeledTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (label.isNotEmpty()) {
            Text(label)
        }
        ThemedOutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            visualTransformation = visualTransformation,
            singleLine = singleLine,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
