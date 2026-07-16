package com.github.mobdev778.aiadventchallenge.presentation.rag.ragconfigscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagConfig
import com.github.mobdev778.aiadventchallenge.presentation.rag.ragconfigscreen.RagConfigScreenEvent
import org.jetbrains.jewel.foundation.theme.LocalContentColor
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.Divider
import org.jetbrains.jewel.ui.component.Text

/**
 * Основной редактируемый блок экрана конфигурации RAG.
 *
 * Объединяет в одной колонке два логических блока настроек:
 * - [FilterSettingsBlock] – параметры фильтрации и ранжирования документов;
 * - [FilePathSettingsBlock] – пути к моделям и токенизаторам.
 *
 * Блоки разделены горизонтальным разделителем [Divider] для визуального
 * отделения.
 *
 * @param ragConfig текущая конфигурация RAG, передаваемая дочерним блокам.
 * @param onEvent обработчик событий экрана, через который дочерние блоки
 *                передают пользовательские взаимодействия на уровень ViewModel
 *                или презентера.
 */
@Composable
fun RagConfigEditor(
    ragConfig: RagConfig,
    onEvent: (RagConfigScreenEvent) -> Unit,
) {
    val labelColor: Color = LocalContentColor.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Настройки RAG")

        FilterSettingsBlock(
            ragConfig = ragConfig,
            labelColor = labelColor,
            onEvent = onEvent,
        )

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            orientation = Orientation.Horizontal,
        )

        FilePathSettingsBlock(
            ragConfig = ragConfig,
            onEvent = onEvent,
        )
    }
}
