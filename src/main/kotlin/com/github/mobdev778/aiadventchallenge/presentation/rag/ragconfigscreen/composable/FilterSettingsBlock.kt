package com.github.mobdev778.aiadventchallenge.presentation.rag.ragconfigscreen.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
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
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.Divider

@Composable
fun FilterSettingsBlock(
    ragConfig: RagConfig,
    labelColor: Color,
    onEvent: (RagConfigScreenEvent) -> Unit,
) {
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
}
