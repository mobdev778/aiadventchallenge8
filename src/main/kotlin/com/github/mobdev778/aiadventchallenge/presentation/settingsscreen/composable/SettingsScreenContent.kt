package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.SettingsScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.model.SettingsScreenState
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.Divider

/**
 * Неоновый цвет текста, используемый в заголовках блоков настроек (LLM, управление контекстом).
 */
@Suppress("MagicNumber")
private val NeonHighlightedTextColor = Color(0xFF04D9FF)

/**
 * Основное содержимое экрана настроек приложения, компонуемое из отдельных секций.
 *
 * Функция собирает полноценный интерфейс, включающий заголовок с кнопкой «Назад»,
 * навигационные кнопки для перехода к профилям, RAG, MCP и MyMCP, блок выбора типа
 * управления контекстом с возможностью настройки лимитов сообщений, блок параметров
 * подключения к LLM (API-ключ, базовый URL, модель) и кнопки действий «Сохранить»/«Сбросить».
 * Все действия пользователя транслируются через единый обработчик событий [onEvent],
 * принимающий экземпляры запечатанного интерфейса [SettingsScreenEvent].
 *
 * @param state Текущее состояние экрана настроек, включая сохранённые данные и редактируемый черновик.
 * @param onEvent Обработчик событий, вызываемый при любом взаимодействии с элементами экрана.
 */
@Composable
fun SettingsScreenContent(
    state: SettingsScreenState,
    onEvent: (SettingsScreenEvent) -> Unit,
) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SettingsHeaderSection(onEvent = onEvent)

        NavigationButtonsRow(onEvent = onEvent)

        Spacer(modifier = Modifier.size(16.dp))

        ContextManagementTypeTypeBlock(
            titleColor = NeonHighlightedTextColor,
            items = state.contextManagementTypes,
            maxMessages = state.draft.maxMessages,
            stickyFactsMaxMessages = state.draft.stickyFactsMaxMessages,
            onEvent = onEvent,
        )

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            orientation = Orientation.Horizontal,
        )

        LlmSettingsBlock(
            apiKey = state.draft.apiKey,
            baseUrl = state.draft.baseUrl,
            baseModel = state.draft.baseModel,
            titleColor = NeonHighlightedTextColor,
            onEvent = onEvent,
        )

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            orientation = Orientation.Horizontal,
        )

        SettingsActionButtons(state = state, onEvent = onEvent)
    }
}
