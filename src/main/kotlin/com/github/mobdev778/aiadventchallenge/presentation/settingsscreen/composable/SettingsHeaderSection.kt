package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.mobdev778.aiadventchallenge.presentation.common.ScreenHeader
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.SettingsScreenEvent
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text

/**
 * Заголовочная секция экрана настроек.
 *
 * Содержит кнопку «Назад», обёрнутую в [Row] с выравниванием по ширине, которая при нажатии отправляет событие
 * [SettingsScreenEvent.OnBackClick]. Ниже отображается экранный заголовок [ScreenHeader] с текстом «Настройки».
 * Компонент используется как верхняя часть пользовательского интерфейса экрана настроек, обеспечивая навигацию
 * и визуальное отделение заголовка.
 *
 * @param onEvent лямбда-приёмник событий экрана настроек. Вызывается при взаимодействии с элементами управления,
 * передавая соответствующий экземпляр [SettingsScreenEvent] для обработки во ViewModel или другом компоненте
 * слоя представления.
 */
@Composable
fun SettingsHeaderSection(onEvent: (SettingsScreenEvent) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedButton(onClick = { onEvent(SettingsScreenEvent.OnBackClick) }) {
            Text("Back")
        }
    }
    ScreenHeader(
        modifier = Modifier.fillMaxWidth(),
        text = "Настройки",
    )
}
