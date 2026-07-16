package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.SettingsScreenEvent
import org.jetbrains.jewel.ui.component.Text

/**
 * Составной блок пользовательского интерфейса, предназначенный для отображения и редактирования
 * основных параметров подключения к большой языковой модели (LLM).
 *
 * Блок содержит заголовок «Настройки LLM:» и три поля ввода с соответствующими метками:
 * - **apiKey** — скрытое поле для ввода API-ключа (с маскированием вводимых символов);
 * - **baseUrl** — поле для указания базового URL эндпоинта LLM;
 * - **baseModel** — поле для ввода идентификатора используемой модели.
 *
 * Каждое изменение текста поля немедленно порождает событие через переданный обработчик
 * [onEvent], с типом события из [SettingsScreenEvent]: [SettingsScreenEvent.OnApiKeyChanged],
 * [SettingsScreenEvent.OnBaseUrlChanged] или [SettingsScreenEvent.OnBaseModelChanged].
 *
 * @param apiKey Текущее значение API-ключа.
 * @param baseUrl Текущее значение базового URL.
 * @param baseModel Текущее значение идентификатора модели.
 * @param titleColor Цвет, которым отрисовывается заголовок блока «Настройки LLM:».
 * @param onEvent Лямбда-обработчик, принимающий экземпляр [SettingsScreenEvent] для уведомления
 *   об изменении любого из параметров.
 */
@Composable
fun LlmSettingsBlock(
    apiKey: String,
    baseUrl: String,
    baseModel: String,
    titleColor: Color,
    onEvent: (SettingsScreenEvent) -> Unit,
) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = "Настройки LLM:",
        color = titleColor,
        textAlign = TextAlign.Center,
    )

    LabeledTextField(
        label = "apiKey",
        value = apiKey,
        onValueChange = { onEvent(SettingsScreenEvent.OnApiKeyChanged(it)) },
        visualTransformation = PasswordVisualTransformation(),
    )

    LabeledTextField(
        label = "baseUrl",
        value = baseUrl,
        onValueChange = { onEvent(SettingsScreenEvent.OnBaseUrlChanged(it)) },
    )

    LabeledTextField(
        label = "baseModel",
        value = baseModel,
        onValueChange = { onEvent(SettingsScreenEvent.OnBaseModelChanged(it)) },
    )
}
