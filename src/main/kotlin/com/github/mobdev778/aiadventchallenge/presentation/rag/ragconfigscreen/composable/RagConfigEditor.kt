package com.github.mobdev778.aiadventchallenge.presentation.rag.ragconfigscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagConfig
import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagFilterType
import com.github.mobdev778.aiadventchallenge.presentation.rag.ragconfigscreen.RagConfigScreenEvent
import com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.composable.BooleanSetting
import com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.composable.FloatSliderSetting
import com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.composable.IntSliderSetting
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.composable.LabeledDropdown
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.composable.LabeledTextField
import org.jetbrains.jewel.foundation.theme.LocalContentColor
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Divider
import org.jetbrains.jewel.ui.component.Text

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

        IntSliderSetting(
            label = "topK до фильтра: ${ragConfig.topKBefore}",
            value = ragConfig.topKBefore,
            valueRange = 1..30,
            onValueChange = { onEvent(RagConfigScreenEvent.OnTopKBeforeChanged(it)) },
        )

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            orientation = Orientation.Horizontal,
        )

        LabeledDropdown(
            label = "тип фильтра",
            labelColor = labelColor,
            selectedText = ragConfig.filterType.name,
            items = RagFilterType.entries.map { it.name },
            onItemSelected = { index ->
                onEvent(RagConfigScreenEvent.OnFilterTypeChanged(RagFilterType.entries[index]))
            },
        )

        BooleanSetting(
            label = "useQueryRewriting",
            checked = ragConfig.useQueryRewriting,
            onCheckedChange = { onEvent(RagConfigScreenEvent.OnUseQueryRewritingChanged(it)) },
        )

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            orientation = Orientation.Horizontal,
        )

        IntSliderSetting(
            label = "topK после фильтра: ${ragConfig.topKAfter}",
            value = ragConfig.topKAfter,
            valueRange = 1..5,
            onValueChange = { onEvent(RagConfigScreenEvent.OnTopKAfterChanged(it)) },
        )

        BooleanSetting(
            label = "Фильтр по минимальному сходству",
            checked = ragConfig.useMinSimilarity,
            onCheckedChange = { onEvent(RagConfigScreenEvent.OnUseMinSimilarityChanged(it)) },
        )

        FloatSliderSetting(
            label = "Мин.допустимое сходство: ${"%.2f".format(ragConfig.minSimilarity)}",
            value = ragConfig.minSimilarity.toFloat(),
            min = 0.1f,
            max = 1.0f,
            onValueChange = { onEvent(RagConfigScreenEvent.OnMinSimilarityChanged(it)) },
        )

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            orientation = Orientation.Horizontal,
        )

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
}

@Composable
private fun FilePathSetting(
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

