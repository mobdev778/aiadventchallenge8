package com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.ui.component.Slider
import org.jetbrains.jewel.ui.component.Text

/**
 * Компонент настройки в виде целочисленного слайдера с подписями границ.
 *
 * Отображает текстовую метку [label] и ползунок, позволяющий выбрать целое значение
 * в пределах [valueRange]. Минимальное и максимальное значения диапазона выводятся
 * по краям ползунка. Компонент удобен для быстрой корректировки числовых параметров.
 *
 * @param label описание изменяемой настройки, отображаемое над слайдером.
 * @param value текущее целочисленное значение.
 * @param valueRange замкнутый целочисленный диапазон, в котором может находиться [value].
 * @param onValueChange коллбэк с новым целым значением при перемещении ползунка пользователем.
 */
@Composable
fun IntSliderSetting(
    label: String,
    value: Int,
    valueRange: IntRange,
    onValueChange: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(label)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = valueRange.first.toString(),
                textAlign = TextAlign.Start,
            )
            Slider(
                value = value.toFloat(),
                onValueChange = { onValueChange(it.toInt()) },
                valueRange = valueRange.first.toFloat()..valueRange.last.toFloat(),
                steps = (valueRange.last - valueRange.first - 1).coerceAtLeast(0),
                modifier = Modifier.weight(1f),
            )
            Text(
                text = valueRange.last.toString(),
                textAlign = TextAlign.End,
            )
        }
    }
}
