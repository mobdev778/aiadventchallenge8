package com.github.mobdev778.aiadventchallenge.presentation.taskcontextscreen.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.ui.component.Text

/**
 * Компонент для экрана контекста задачи, отображающий элемент списка дел (задач).
 *
 * Каждый элемент представляет собой строку с порядковым номером, названием и индикатором
 * выполнения: отмеченный элемент сопровождается галочкой ✅, неотмеченный — пустым
 * заполнителем.
 */
@Composable
fun CheckedItem(
    /**
     * Модификатор компоновки, применяемый к корневому контейнеру [Row].
     */
    modifier: Modifier = Modifier,
    /**
     * Флаг, указывающий, отмечен ли данный элемент как выполненный.
     * Если `true` — отображается маркер ✅, иначе — пустой заполнитель.
     */
    checked: Boolean,
    /**
     * Порядковый номер элемента в списке (начинается с 1).
     */
    index: Int,
    /**
     * Название элемента (описание задачи или пункта списка).
     */
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
