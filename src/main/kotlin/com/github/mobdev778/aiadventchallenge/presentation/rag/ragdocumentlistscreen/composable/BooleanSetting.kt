package com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.ui.component.Checkbox
import org.jetbrains.jewel.ui.component.Text

/**
 * Компонент элемента настройки, представляющий собой горизонтальный ряд с чекбоксом и текстовой меткой.
 *
 * Используется для отображения логической опции в интерфейсе RAG-документов, позволяя пользователю включать или выключать
 * определённую настройку. Чекбокс и метка выровнены по центру по вертикали с фиксированным отступом.
 *
 * @param label текстовая метка, описывающая настройку.
 * @param checked текущее состояние чекбокса (включен/выключен).
 * @param onCheckedChange лямбда-обработчик изменения состояния, вызывается при переключении чекбокса пользователем.
 */
@Composable
fun BooleanSetting(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
        )
        Text(label)
    }
}
