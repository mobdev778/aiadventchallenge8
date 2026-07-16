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
 * Шаг изменения значения слайдера для чисел с плавающей точкой.
 */
private const val FLOAT_SLIDER_STEP_INCREMENT = 0.05f

/**
 * Компонент настройки с ползунком для выбора значения с плавающей точкой.
 *
 * Используется на экране списка документов RAG для задания числовых параметров
 * с визуальной обратной связью в виде метки, минимального и максимального значений,
 * а также дискретного слайдера.
 *
 * @param label текстовая метка настройки, отображаемая над слайдером.
 * @param value текущее выбранное значение в диапазоне [min]..[max].
 * @param min минимально допустимое значение.
 * @param max максимально допустимое значение.
 * @param onValueChange callback, вызываемый при изменении положения слайдера,
 *        получает новое значение типа [Double].
 */
@Composable
fun FloatSliderSetting(
    label: String,
    value: Float,
    min: Float,
    max: Float,
    onValueChange: (Double) -> Unit,
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
                text = min.toString(),
                textAlign = TextAlign.Start,
            )
            var steps = 0
            var current = min
            while (current <= max) {
                current += FLOAT_SLIDER_STEP_INCREMENT
                steps++
            }

            Slider(
                value = value.toFloat(),
                onValueChange = { onValueChange(it.toDouble()) },
                valueRange = min..max,
                steps = steps,
                modifier = Modifier.weight(1f),
            )

            Text(
                text = max.toString(),
                textAlign = TextAlign.End,
            )
        }
    }
}
