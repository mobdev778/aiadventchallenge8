package com.github.mobdev778.aiadventchallenge.presentation.rag.ragconfigscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.composable.LabeledTextField
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text

/**
 * Composable-компонент для отображения настройки пути к файлу.
 *
 * Предоставляет метку, поле ввода (реализованное через [LabeledTextField]) и кнопку «Выбрать...»,
 * позволяющую пользователю вручную ввести путь или инициировать диалог выбора файла.
 *
 * @param label Отображаемый текст метки над элементом управления.
 * @param value Текущее значение (путь к файлу), отображаемое в поле ввода.
 * @param onValueChange Функция обратного вызова, вызываемая при изменении текста в поле ввода.
 * @param onChooseClick Функция обратного вызова, вызываемая при нажатии на кнопку «Выбрать...».
 */
@Composable
fun FilePathSetting(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onChooseClick: () -> Unit,
) {
    Column {
        Text(label)

        Spacer(modifier = Modifier.size(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            LabeledTextField(
                label = "",
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
            )
            DefaultButton(
                onClick = onChooseClick,
            ) {
                Text("Выбрать...")
            }
        }
    }
}
