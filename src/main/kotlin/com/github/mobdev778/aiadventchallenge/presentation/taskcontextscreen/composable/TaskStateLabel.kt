package com.github.mobdev778.aiadventchallenge.presentation.taskcontextscreen.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.github.mobdev778.aiadventchallenge.domain.task.TaskState
import com.github.mobdev778.aiadventchallenge.presentation.common.taskStateColors
import org.jetbrains.jewel.ui.component.Text

/**
 * Композабл, отображающий метку состояния задачи.
 *
 * Выводит строку "Состояние: " и название текущего состояния [state], выделенное полужирным шрифтом.
 * Цвет текста состояния определяется по индексу [TaskState.ordinal] из палитры цветов [taskStateColors],
 * что позволяет визуально различать этапы жизненного цикла задачи.
 *
 * @param state Текущее состояние задачи из перечисления [TaskState]. Задаёт отображаемый текст и его окраску.
 */
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
