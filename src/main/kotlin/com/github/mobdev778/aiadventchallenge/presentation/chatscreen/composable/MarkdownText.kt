package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Composable-компонент для отображения Markdown-текста.
 *
 * Принимает строку с разметкой Markdown, парсит её в список блоков [MarkdownBlock]
 * с помощью [MarkdownParser.parse] и отрисовывает каждый блок через [RenderBlock].
 * Цвет [color] используется в качестве цвета заголовков (`headingColor`).
 *
 * Для экономии ресурсов результат парсинга запоминается через `remember` и не вычисляется повторно
 * при рекомпозиции, пока не изменится исходная строка.
 *
 * @param text Исходный Markdown-текст.
 * @param color Цвет, применяемый для заголовков.
 * @param modifier Модификатор, применяемый к корневому контейнеру [Column].
 *                 По умолчанию растягивается на всю доступную ширину.
 */
@Composable
fun MarkdownText(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val blocks = remember(text) { MarkdownParser.parse(text) }

    Column(modifier = modifier.fillMaxWidth()) {
        blocks.forEachIndexed { index, block ->
            RenderBlock(
                block = block,
                headingColor = color,
                isLast = index == blocks.lastIndex,
            )
        }
    }
}
