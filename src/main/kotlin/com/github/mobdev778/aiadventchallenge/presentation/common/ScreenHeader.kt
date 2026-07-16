package com.github.mobdev778.aiadventchallenge.presentation.common

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.ui.component.Text

/**
 * Экранный заголовок с неоновой подсветкой.
 *
 * Компонент, отображающий текст заголовка в прямоугольной рамке со скруглёнными углами,
 * выровненный по центру и окрашенный в яркий неоновый цвет.
 * Используется в качестве визуального разделителя и привлечения внимания к ключевым экранам.
 *
 * @param modifier Модификатор, применяемый к корневому контейнеру заголовка.
 * @param text Текст, отображаемый внутри заголовка.
 */
@Composable
fun ScreenHeader(
    modifier: Modifier = Modifier,
    text: String,
) {
    @Suppress("MagicNumber")
    val neonHighlightedText = Color(0xFF04D9FF)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = neonHighlightedText,
                    shape = RoundedCornerShape(10.dp),
                )
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = text,
                textAlign = TextAlign.Center,
                color = neonHighlightedText,
            )
        }
    }
}
