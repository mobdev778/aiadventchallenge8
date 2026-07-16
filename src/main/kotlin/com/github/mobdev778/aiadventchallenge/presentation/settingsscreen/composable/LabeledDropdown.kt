package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.foundation.theme.LocalContentColor
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text

/**
 * Легковесный выпадающий список на основе [OutlinedButton], не зависящий от API Dropdown библиотеки Jewel
 * (которое может различаться между версиями).
 *
 * Компонент отображает метку, кнопку с текущим выбранным значением и раскрывающуюся панель
 * со списком доступных вариантов. При выборе элемента вызывается callback [onItemSelected],
 * после чего панель сворачивается.
 *
 * Используется на экране настроек для предоставления пользователю возможности выбора
 * из фиксированного набора значений.
 *
 * @param label Текстовая метка, отображаемая над выпадающим списком.
 * @param labelColor Цвет текста метки.
 * @param selectedText Текст, отображаемый на кнопке и представляющий текущий выбранный элемент.
 * @param items Список строк — доступных для выбора вариантов.
 * @param onItemSelected Callback, вызываемый при выборе элемента. Принимает индекс выбранного элемента в списке [items].
 */
@Composable
fun LabeledDropdown(
    label: String,
    labelColor: Color,
    selectedText: String,
    items: List<String>,
    onItemSelected: (index: Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = label,
            color = labelColor,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        // Lightweight dropdown without relying on Jewel's Dropdown API (it differs between versions).
        OutlinedButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(selectedText)
        }

        if (expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = LocalContentColor.current.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(10.dp),
                    )
                    .background(
                        color = LocalContentColor.current.copy(alpha = 0.06f),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                    )
                    .padding(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items.forEachIndexed { index, item ->
                    OutlinedButton(
                        onClick = {
                            onItemSelected(index)
                            expanded = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(item)
                    }
                }
            }
        }
    }
}
