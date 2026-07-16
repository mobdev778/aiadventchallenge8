package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.ui.component.Text

/**
 * Составной компонент, объединяющий текстовую метку и тематизированное текстовое поле
 * [ThemedOutlinedTextField] в единую структуру. Обеспечивает визуальное согласование
 * с текущей темой IDE и упрощает построение форм в экранах настроек.
 *
 * Компонент отрисовывает переданную текстовую метку (если она не пуста),
 * а под ней — многоцелевое поле ввода, адаптированное под контурный стиль
 * и цветовую схему IntelliJ Jewel.
 *
 * @param label Текстовая метка, отображаемая над полем ввода. Если строка пустая,
 *   метка не рисуется.
 * @param value Текущее текстовое значение поля ввода.
 * @param onValueChange Callback, вызываемый при каждом изменении текста пользователем.
 * @param modifier [Modifier], применяемый к корневому контейнеру [Column].
 *   По умолчанию — [Modifier.Companion] (без дополнительных модификаторов).
 * @param singleLine Флаг, ограничивающий поле ввода одной строкой.
 *   По умолчанию `true`.
 * @param visualTransformation Визуальная трансформация отображаемого текста
 *   (например, маскирование пароля). По умолчанию — [VisualTransformation.None].
 */
@Composable
fun LabeledTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (label.isNotEmpty()) {
            Text(label)
        }
        ThemedOutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            visualTransformation = visualTransformation,
            singleLine = singleLine,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
