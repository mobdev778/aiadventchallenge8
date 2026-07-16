package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.github.mobdev778.aiadventchallenge.domain.settings.model.ContextManagementType
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.SettingsScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.model.ContextManagementTypeUi

/**
 * Составной блок экрана настроек, который отображает выпадающий список для выбора
 * способа ограничения контекстного окна и, в зависимости от выбранного типа,
 * соответствующие поля ввода максимального количества сообщений.
 *
 * Блок интегрируется с [SettingsScreenEvent] для обработки действий пользователя.
 *
 * @param items список [ContextManagementTypeUi], представляющих доступные типы управления контекстом
 *   с информацией о текущем выборе.
 * @param titleColor цвет, используемый для меток и заголовков.
 * @param maxMessages текущее значение максимального количества сообщений для режима «Скользящее окно».
 * @param stickyFactsMaxMessages текущее значение максимального количества сообщений для режима «Закреплённые факты».
 * @param onEvent обработчик событий экрана настроек, принимающий [SettingsScreenEvent].
 */
@Composable
fun ContextManagementTypeTypeBlock(
    items: List<ContextManagementTypeUi>,
    titleColor: Color,
    maxMessages: Int,
    stickyFactsMaxMessages: Int,
    onEvent: (SettingsScreenEvent) -> Unit,
) {
    val selectedItem = items.first { it.selected }

    LabeledDropdown(
        label = "Способ ограничения контекстного окна:",
        labelColor = titleColor,
        selectedText = selectedItem.label,
        items = items.map { it.label },
        onItemSelected = { index ->
            onEvent(
                SettingsScreenEvent.OnContextManagementTypeChanged(
                    items[index].type
                )
            )
        },
    )

    if (selectedItem.type == ContextManagementType.SlidingWindow) {
        LabeledTextField(
            label = "Max сообщений",
            value = maxMessages.toString(),
            onValueChange = { onEvent(SettingsScreenEvent.OnMaxMessagesChanged(it)) },
        )
    }

    if (selectedItem.type == ContextManagementType.StickyFacts) {
        LabeledTextField(
            label = "Max сообщений",
            value = stickyFactsMaxMessages.toString(),
            onValueChange = { onEvent(SettingsScreenEvent.OnStickyFactsMaxMessagesChanged(it)) },
        )
    }
}
