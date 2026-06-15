package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.foundation.theme.LocalContentColor
import org.jetbrains.jewel.foundation.theme.LocalTextStyle

@Composable
fun ThemedOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier.Companion,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    val shape = RoundedCornerShape(10.dp)
    val contentColor = LocalContentColor.current

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = LocalTextStyle.current.copy(color = contentColor),
        cursorBrush = SolidColor(contentColor),
        visualTransformation = visualTransformation,
        singleLine = singleLine,
        modifier = modifier
            .border(
                width = 1.dp,
                color = contentColor.copy(alpha = 0.35f),
                shape = shape,
            )
            .background(
                color = contentColor.copy(alpha = 0.06f),
                shape = shape,
            )
            .padding(horizontal = 10.dp, vertical = 8.dp),
    )
}