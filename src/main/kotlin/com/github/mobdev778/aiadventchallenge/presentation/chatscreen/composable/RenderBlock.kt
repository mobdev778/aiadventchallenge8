package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.jewel.ui.component.Text

@Suppress("MagicNumber")
private val SimpleTextColor = Color(0xFFA0A0A0)

/**
 * Composable-функция, отвечающая за отрисовку одного структурного блока разметки Markdown
 * внутри экрана чата.
 *
 * В зависимости от типа [MarkdownBlock] применяет соответствующее оформление, шрифты, отступы
 * и цвет. Поддерживает абзацы, заголовки (1–3 уровней с каскадным размером текста),
 * маркированные и нумерованные списки с учётом вложенности, а также блоки кода с подсветкой синтаксиса.
 * Нижний отступ добавляется только в том случае, если блок не является последним ([isLast] == false),
 * чтобы избежать лишнего пространства после последнего элемента чата.
 *
 * @param block Блок разметки, наследующий [MarkdownBlock] (может быть [MarkdownBlock.Paragraph],
 *   [MarkdownBlock.Heading], [MarkdownBlock.BulletItem], [MarkdownBlock.NumberedItem] или [MarkdownBlock.CodeFence]).
 * @param headingColor Цвет, используемый для текста заголовков.
 * @param isLast Флаг, сигнализирующий о том, что данный блок является последним в списке отрисовки.
 *   При значении `true` нижний отступ не применяется.
 */
@Composable
fun RenderBlock(
    block: MarkdownBlock,
    headingColor: Color,
    isLast: Boolean,
) {
    when (block) {
        is MarkdownBlock.Paragraph -> {
            Text(
                text = block.text,
                color = SimpleTextColor,
                modifier = Modifier.padding(bottom = if (isLast) 0.dp else 6.dp),
            )
        }

        is MarkdownBlock.Heading -> {
            Text(
                text = block.text,
                color = headingColor,
                fontWeight = FontWeight.Bold,
                fontSize = when (block.level) {
                    1 -> 22.sp
                    2 -> 18.sp
                    else -> 16.sp
                },
                modifier = Modifier.padding(bottom = if (isLast) 0.dp else 8.dp),
            )
        }

        is MarkdownBlock.BulletItem -> {
            Text(
                text = buildAnnotatedString {
                    append("• ")
                    append(block.text)
                },
                color = SimpleTextColor,
                modifier = Modifier.padding(
                    start = (block.level * 12).dp,
                    bottom = if (isLast) 0.dp else 4.dp,
                ),
            )
        }

        is MarkdownBlock.NumberedItem -> {
            Text(
                text = buildAnnotatedString {
                    append("${block.number}. ")
                    append(block.text)
                },
                color = SimpleTextColor,
                modifier = Modifier.padding(
                    start = (block.level * 12).dp,
                    bottom = if (isLast) 0.dp else 4.dp,
                ),
            )
        }

        is MarkdownBlock.CodeFence -> {
            CodeFenceBlock(
                code = block.code,
                modifier = Modifier.padding(bottom = if (isLast) 0.dp else 6.dp),
            )
        }
    }
}
