package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model.ContextManagementState
import org.jetbrains.jewel.ui.component.Text

@Suppress("MagicNumber")
private val ContextManagementIndicatorBackgroundColor = Color(0xFF2B2B2B)

private const val HIGH_USAGE_THRESHOLD = 0.75
private const val MEDIUM_USAGE_THRESHOLD = 0.50

@Suppress("MagicNumber")
private val HighUsageColor = Color(0xFFFE019A)

@Suppress("MagicNumber")
private val MediumUsageColor = Color(0xFFFF5C00)

@Suppress("MagicNumber")
private val LowUsageColor = Color(0xFF39FF14)

@Composable
fun ContextManagementIndicator(
    state: ContextManagementState,
    modifier: Modifier = Modifier,
) {
    val text = indicatorText(state)
    val usedRatio = usedRatio(state)
    val indicatorColor = indicatorColor(usedRatio)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = Color.White,
                    shape = RoundedCornerShape(10.dp),
                )
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = text,
                color = indicatorColor,
            )
            if (usedRatio != null) {
                IndicatorProgressBar(
                    progress = usedRatio.toFloat().coerceIn(0f, 1f),
                    indicatorColor = indicatorColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 8.dp,
                            start = 8.dp,
                            end = 8.dp,
                            bottom = 8.dp
                        ),
                )
            }
        }
    }
}

private fun indicatorText(state: ContextManagementState): String {
    return when (state) {
        is ContextManagementState.None -> {
            "- нет ограничения контекста -"
        }

        is ContextManagementState.SlidingWindow -> {
            "Sliding Window: ${state.messages} из ${state.maxMessages} сообщений"
        }

        is ContextManagementState.StickFacts -> {
            "Sticky Facts: ${state.messages} из ${state.maxMessages} сообщений"
        }

        is ContextManagementState.Branching -> {
            "Branching"
        }
    }
}

private fun usedRatio(state: ContextManagementState): Double? {
    return when (state) {
        is ContextManagementState.SlidingWindow -> {
            if (state.maxMessages <= 0) null else state.messages.toDouble() / state.maxMessages.toDouble()
        }

        is ContextManagementState.StickFacts -> {
            if (state.maxMessages <= 0) null else state.messages.toDouble() / state.maxMessages.toDouble()
        }

        else -> null
    }
}

private fun indicatorColor(usedRatio: Double?): Color {
    val ratio = usedRatio ?: return Color.White
    return when {
        ratio >= HIGH_USAGE_THRESHOLD -> HighUsageColor
        ratio >= MEDIUM_USAGE_THRESHOLD -> MediumUsageColor
        else -> LowUsageColor
    }
}

@Composable
private fun IndicatorProgressBar(
    progress: Float,
    indicatorColor: Color,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(999.dp)

    Box(
        modifier = modifier
            .background(
                color = ContextManagementIndicatorBackgroundColor,
                shape = shape,
            )
            .border(
                width = 1.dp,
                color = Color.White,
                shape = shape,
            )
            .padding(2.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .background(
                    color = indicatorColor,
                    shape = shape,
                )
                .padding(vertical = 3.dp),
        )
    }
}
