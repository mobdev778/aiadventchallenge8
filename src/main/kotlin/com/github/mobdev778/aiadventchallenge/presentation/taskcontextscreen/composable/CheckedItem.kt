package com.github.mobdev778.aiadventchallenge.presentation.taskcontextscreen.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.ui.component.Text

@Composable
fun CheckedItem(
    modifier: Modifier = Modifier,
    checked: Boolean,
    index: Int,
    name: String
) {
    Row(modifier = modifier) {
        if (checked) {
            Text(
                text = "✅",
            )
        } else {
            Text(
                text = " ",
                color = Color.White,
            )
        }
        Spacer(modifier = Modifier.size(12.dp))
        Text("${index}. $name")
    }
}