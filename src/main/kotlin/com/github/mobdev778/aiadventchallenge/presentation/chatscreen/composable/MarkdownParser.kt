package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.composable

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle

private const val BULLET_INDENT_DIVISOR = 2

private const val REGEX_CONTENT_GROUP_INDEX = 3

private const val INLINE_CODE_BACKGROUND_ARGB = 0x33FFFFFFL

/**
 * Парсер Markdown-разметки в список визуальных блоков для отображения в чате.
 *
 * Преобразует переданную строку с Markdown-разметкой в список экземпляров [MarkdownBlock],
 * готовых к рендерингу с помощью Compose-компонентов. Поддерживает заголовки (1–3 уровня),
 * маркированные и нумерованные списки с отступами, ограждённые блоки кода, а также
 * базовое инлайн-форматирование: жирный, курсив, зачёркнутый текст и встроенный код.
 *
 * Используется для оформления сообщений на экране чата.
 */
object MarkdownParser {
    private val headingRegex = Regex("^(#{1,3})\\s+(.*)$")
    private val bulletRegex = Regex("^(\\s*)([-*+])\\s+(.*)$")
    private val numberedRegex = Regex("^(\\s*)(\\d+)\\.\\s+(.*)$")

    /**
     * Разбирает строку Markdown в список блоков для последующей отрисовки.
     *
     * @param markdown Исходная строка в формате Markdown.
     * @return Список блоков [MarkdownBlock], упорядоченных по мере появления в тексте.
     *         Для пустой или состоящей только из пробелов строки возвращается список
     *         с одним пустым параграфом.
     */
    fun parse(markdown: String): List<MarkdownBlock> {
        if (markdown.isBlank()) return listOf(MarkdownBlock.Paragraph(AnnotatedString("")))

        val normalized = markdown.replace("\r\n", "\n")
        val lines = normalized.split('\n')
        val blocks = mutableListOf<MarkdownBlock>()
        val paragraphLines = mutableListOf<String>()
        val codeLines = mutableListOf<String>()

        var inCodeFence = false

        fun flushParagraph() {
            if (paragraphLines.isEmpty()) return
            val paragraph = paragraphLines.joinToString("\n").trimEnd()
            if (paragraph.isNotBlank()) {
                blocks += MarkdownBlock.Paragraph(parseInline(paragraph))
            }
            paragraphLines.clear()
        }

        fun flushCodeFence() {
            blocks += MarkdownBlock.CodeFence(codeLines.joinToString("\n"))
            codeLines.clear()
        }

        lines.forEach { rawLine ->
            val line = rawLine.trimEnd()

            if (line.startsWith("```")) {
                flushParagraph()
                if (inCodeFence) flushCodeFence()
                inCodeFence = !inCodeFence
                return@forEach
            }

            if (inCodeFence) {
                codeLines += rawLine
                return@forEach
            }

            if (line.isBlank()) {
                flushParagraph()
                return@forEach
            }

            val matchedBlock = matchBlock(rawLine, line)
            if (matchedBlock != null) {
                flushParagraph()
                blocks += matchedBlock
                return@forEach
            }

            paragraphLines += rawLine
        }

        flushParagraph()
        if (inCodeFence) flushCodeFence()

        return blocks.ifEmpty { listOf(MarkdownBlock.Paragraph(androidx.compose.ui.text.AnnotatedString(""))) }
    }

    private fun matchBlock(rawLine: String, line: String): MarkdownBlock? {
        var result: MarkdownBlock? = null

        headingRegex.matchEntire(line)?.let { match ->
            val level = match.groupValues[1].length
            val content = match.groupValues[2]
            result = MarkdownBlock.Heading(text = parseInline(content), level = level)
        }

        if (result == null) {
            bulletRegex.matchEntire(rawLine)?.let { match ->
                val indent = match.groupValues[1].length
                val content = match.groupValues[REGEX_CONTENT_GROUP_INDEX]
                result = MarkdownBlock.BulletItem(
                    text = parseInline(content),
                    level = indent / BULLET_INDENT_DIVISOR,
                )
            }
        }

        if (result == null) {
            numberedRegex.matchEntire(rawLine)?.let { match ->
                val indent = match.groupValues[1].length
                val number = match.groupValues[2].toIntOrNull() ?: 1
                val content = match.groupValues[REGEX_CONTENT_GROUP_INDEX]
                result = MarkdownBlock.NumberedItem(
                    number = number,
                    text = parseInline(content),
                    level = indent / BULLET_INDENT_DIVISOR,
                )
            }
        }

        return result
    }

    private fun parseInline(text: String): AnnotatedString {
        return buildAnnotatedString {
            var index = 0
            while (index < text.length) {
                val result = tryParseInlineStyle(text, index)
                if (result != null) {
                    withStyle(result.spanStyle) {
                        append(result.content)
                    }
                    index = result.nextIndex
                } else {
                    append(text[index])
                    index += 1
                }
            }
        }
    }

    private data class InlineStyleResult(
        val content: AnnotatedString,
        val spanStyle: SpanStyle,
        val nextIndex: Int,
    )

    private fun tryParseInlineStyle(text: String, index: Int): InlineStyleResult? {
        return tryParseBoldItalic(text, index, "**", FontWeight.Bold, null)
            ?: tryParseBoldItalic(text, index, "*", null, FontStyle.Italic)
            ?: tryParseInlineCode(text, index)
            ?: tryParseStrikethrough(text, index)
    }

    private fun tryParseBoldItalic(
        text: String,
        index: Int,
        delimiter: String,
        fontWeight: FontWeight?,
        fontStyle: FontStyle?,
    ): InlineStyleResult? {
        val hasPrefix = text.startsWith(delimiter, index)
        val closing = if (hasPrefix) text.indexOf(delimiter, startIndex = index + delimiter.length) else -1
        val result = if (hasPrefix && closing > index + delimiter.length) {
            val inner = parseInline(text.substring(index + delimiter.length, closing))
            InlineStyleResult(
                content = inner,
                spanStyle = SpanStyle(fontWeight = fontWeight, fontStyle = fontStyle),
                nextIndex = closing + delimiter.length,
            )
        } else null
        return result
    }

    private fun tryParseInlineCode(text: String, index: Int): InlineStyleResult? {
        val hasPrefix = text.startsWith("`", index)
        val closing = if (hasPrefix) text.indexOf('`', startIndex = index + 1) else -1
        val result = if (hasPrefix && closing > index + 1) {
            InlineStyleResult(
                content = androidx.compose.ui.text.AnnotatedString(text.substring(index + 1, closing)),
                spanStyle = SpanStyle(
                    fontFamily = FontFamily.Monospace,
                    background = colorWithAlpha(INLINE_CODE_BACKGROUND_ARGB),
                ),
                nextIndex = closing + 1,
            )
        } else null
        return result
    }

    private fun tryParseStrikethrough(text: String, index: Int): InlineStyleResult? {
        val hasPrefix = text.startsWith("~~", index)
        val closing = if (hasPrefix) text.indexOf("~~", startIndex = index + 2) else -1
        val result = if (hasPrefix && closing > index + 2) {
            val inner = parseInline(text.substring(index + 2, closing))
            InlineStyleResult(
                content = inner,
                spanStyle = SpanStyle(textDecoration = TextDecoration.LineThrough),
                nextIndex = closing + 2,
            )
        } else null
        return result
    }

    private fun colorWithAlpha(argb: Long): Color {
        return Color(argb)
    }
}
