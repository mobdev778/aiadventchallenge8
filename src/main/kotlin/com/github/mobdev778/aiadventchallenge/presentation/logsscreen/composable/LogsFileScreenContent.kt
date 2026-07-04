package com.github.mobdev778.aiadventchallenge.presentation.logsscreen.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.presentation.common.ScreenHeader
import com.github.mobdev778.aiadventchallenge.presentation.logsscreen.LogsFileScreenState
import org.jetbrains.jewel.ui.component.Text

@Suppress("MagicNumber")
private val NeonHighlightedTextColor = Color(0xFF04D9FF)

@Composable
fun LogsFileScreenContent(
    state: LogsFileScreenState,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ScreenHeader(
            modifier = Modifier.fillMaxWidth(),
            text = if (state.fileName.isBlank()) "Logs" else "Logs: ${state.fileName}",
        )

        if (state.logs.isEmpty()) {
            Text(
                text = "Логи за текущий день пока отсутствуют",
                color = NeonHighlightedTextColor,
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                itemsIndexed(state.logs) { index, line ->
                    Text(
                        text = line.ifEmpty { " " },
                        color = NeonHighlightedTextColor,
                        fontFamily = FontFamily.Monospace,
                    )
                }
            }
        }
    }
}
