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
import com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model.TokenLimitState
import org.jetbrains.jewel.ui.component.Text

@Composable
fun ChatLimitIndicator(
    state: TokenLimitState,
    modifier: Modifier = Modifier,
) {
    val text = limitIndicatorText(state)
    val usedRatio = limitIndicatorUsedRatio(state)
    val indicatorColor = limitIndicatorColor(usedRatio)

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
            if (usedRatio != null) {
                LimitProgressBar(
                    progress = usedRatio.toFloat().coerceIn(0f, 1f),
                    indicatorColor = indicatorColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                )
            }

            Text(
                text = text,
                color = indicatorColor,
            )
        }
    }
}

private fun limitIndicatorText(state: TokenLimitState): String {
    return when (state) {
        is TokenLimitState.FullHistory -> {
            "лимит не используется"
        }

        is TokenLimitState.LimitMessages -> {
            "${state.messages} из ${state.maxMessages} сообщений"
        }

        is TokenLimitState.LimitTokens -> {
            "${state.tokens} из ${state.maxTokens} токенов"
        }
    }
}

private fun limitIndicatorUsedRatio(state: TokenLimitState): Double? {
    return when (state) {
        is TokenLimitState.FullHistory -> null

        is TokenLimitState.LimitMessages -> {
            if (state.maxMessages <= 0) null else state.messages.toDouble() / state.maxMessages.toDouble()
        }

        is TokenLimitState.LimitTokens -> {
            if (state.maxTokens <= 0) null else state.tokens.toDouble() / state.maxTokens.toDouble()
        }
    }
}

private fun limitIndicatorColor(usedRatio: Double?): Color {
    val ratio = usedRatio ?: return Color.White
    return when {
        ratio >= 0.75 -> Color.Red
        ratio >= 0.50 -> Color.Yellow
        else -> Color.Green
    }
}

@Composable
private fun LimitProgressBar(
    progress: Float,
    indicatorColor: Color,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(999.dp)

    Box(
        modifier = modifier
            .background(
                color = Color(0xFF2B2B2B),
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
