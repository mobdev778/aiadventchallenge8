package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
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
    var fieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = inputText,
                selection = TextRange(inputText.length),
            ),
        )
    }

    // Keep internal TextFieldValue in sync with external state.
    // If text length increases (e.g. programmatic append), move cursor to the end.
    LaunchedEffect(inputText) {
        val oldText = fieldValue.text
        val shouldMoveCursorToEnd = inputText.length > oldText.length

        fieldValue = if (shouldMoveCursorToEnd) {
            TextFieldValue(
                text = inputText,
                selection = TextRange(inputText.length),
            )
        } else {
            fieldValue.copy(text = inputText)
        }
    }

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
            value = fieldValue,
            onValueChange = { newValue ->
                fieldValue = newValue
                onInputTextChanged(newValue.text)
            },
            textStyle = LocalTextStyle.current.copy(color = contentColor),
            cursorBrush = SolidColor(contentColor),
            modifier = Modifier
                .weight(1f)
                .onKeyEvent { event ->
                    if (event.type == KeyEventType.KeyUp && event.key == Key.Enter) {
                        onSendClick()
                        true
                    } else {
                        false
                    }
                }
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
            Text("Отправить")
        }
    }
}