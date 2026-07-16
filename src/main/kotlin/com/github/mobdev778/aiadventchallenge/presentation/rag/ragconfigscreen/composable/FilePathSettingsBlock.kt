package com.github.mobdev778.aiadventchallenge.presentation.rag.ragconfigscreen.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagConfig
import com.github.mobdev778.aiadventchallenge.presentation.rag.ragconfigscreen.RagConfigScreenEvent
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.Divider

/**
 * Блок настроек путей к файлам моделей и токенизаторов конфигурации RAG.
 *
 * Содержит две группы элементов [FilePathSetting]:
 * - для модели-реранкера и её токенизатора;
 * - для модели эмбеддингов и её токенизатора.
 *
 * Группы разделены горизонтальным разделителем.
 *
 * @param ragConfig Текущая конфигурация RAG, из которой извлекаются значения путей.
 * @param onEvent Функция-обработчик событий экрана, вызываемая при изменении путей
 *                или инициировании выбора файла.
 */
@Composable
fun FilePathSettingsBlock(
    ragConfig: RagConfig,
    onEvent: (RagConfigScreenEvent) -> Unit,
) {
    FilePathSetting(
        label = "Ranker модель (model_quantized.onnx)",
        value = ragConfig.rankerModelPath,
        onValueChange = { onEvent(RagConfigScreenEvent.OnRankerModelPathChanged(it)) },
        onChooseClick = { onEvent(RagConfigScreenEvent.OnChooseRankerModelPathClick) },
    )

    FilePathSetting(
        label = "Ranker tokenizer (tokenizer.json)",
        value = ragConfig.rankerTokenizerPath,
        onValueChange = { onEvent(RagConfigScreenEvent.OnRankerTokenizerPathChanged(it)) },
        onChooseClick = { onEvent(RagConfigScreenEvent.OnChooseRankerTokenizerPathClick) },
    )

    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        orientation = Orientation.Horizontal,
    )

    FilePathSetting(
        label = "Embedding модели (model_quantized.onnx)",
        value = ragConfig.embModelPath,
        onValueChange = { onEvent(RagConfigScreenEvent.OnEmbModelPathChanged(it)) },
        onChooseClick = { onEvent(RagConfigScreenEvent.OnChooseEmbModelPathClick) },
    )

    FilePathSetting(
        label = "Embedding tokenizer (tokenizer.json)",
        value = ragConfig.embTokenizerPath,
        onValueChange = { onEvent(RagConfigScreenEvent.OnEmbTokenizerPathChanged(it)) },
        onChooseClick = { onEvent(RagConfigScreenEvent.OnChooseEmbTokenizerPathClick) },
    )
}
