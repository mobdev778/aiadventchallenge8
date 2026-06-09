package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.foundation.theme.LocalContentColor
import org.jetbrains.jewel.foundation.theme.LocalTextStyle
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text

@Composable
fun ChatInputArea(
    inputText: String,
    modifier: Modifier = Modifier,
    onInputTextChanged: (String) -> Unit,
    onSendClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val shape = RoundedCornerShape(10.dp)

        // Use IDE theme-aware colors from Jewel.
        // LocalContentColor is usually correct for text on the current surface.
        val contentColor = LocalContentColor.current

        BasicTextField(
            value = inputText,
            onValueChange = onInputTextChanged,
            textStyle = LocalTextStyle.current.copy(color = contentColor),
            cursorBrush = SolidColor(contentColor),
            modifier = Modifier
                .weight(1f)
                // Rounded outline
                .border(
                    width = 1.dp,
                    color = contentColor.copy(alpha = 0.35f),
                    shape = shape,
                )
                // Slight fill so the field is visible on both light/dark themes
                .background(
                    color = contentColor.copy(alpha = 0.06f),
                    shape = shape,
                )
                .padding(horizontal = 10.dp, vertical = 8.dp),
        )

        OutlinedButton(
            onClick = onSendClick,
        ) {
            Text("Send")
        }
    }
}