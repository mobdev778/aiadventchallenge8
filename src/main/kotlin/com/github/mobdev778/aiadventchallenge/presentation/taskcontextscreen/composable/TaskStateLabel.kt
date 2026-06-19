package com.github.mobdev778.aiadventchallenge.presentation.taskcontextscreen.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.github.mobdev778.aiadventchallenge.domain.task.TaskState
import com.github.mobdev778.aiadventchallenge.presentation.common.taskStateColors
import org.jetbrains.jewel.ui.component.Text

@Composable
fun TaskStateLabel(
    state: TaskState,
) {
    Row {
        Text("Состояние: ")
        Text(
            text = "$state",
            color = taskStateColors[state.ordinal],
            fontWeight = FontWeight.Bold
        )
    }
}
