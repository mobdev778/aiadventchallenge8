package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.jewel.ui.component.Text

@Composable
fun MarkdownText(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
) {

    val blocks = remember(text) { MarkdownParser.parse(text) }
    val simpleText = Color(0xFFA0A0A0)

    Column(modifier = modifier.fillMaxWidth()) {
        blocks.forEachIndexed { index, block ->
            when (block) {
                is MarkdownBlock.Paragraph -> {
                    Text(
                        text = block.text,
                        color = simpleText,
                        modifier = Modifier.padding(bottom = if (index == blocks.lastIndex) 0.dp else 6.dp),
                    )
                }

                is MarkdownBlock.Heading -> {
                    Text(
                        text = block.text,
                        color = color,
                        fontWeight = FontWeight.Bold,
                        fontSize = when (block.level) {
                            1 -> 22.sp
                            2 -> 18.sp
                            else -> 16.sp
                        },
                        modifier = Modifier.padding(bottom = if (index == blocks.lastIndex) 0.dp else 8.dp),
                    )
                }

                is MarkdownBlock.BulletItem -> {
                    Text(
                        text = buildAnnotatedString {
                            append("• ")
                            append(block.text)
                        },
                        color = simpleText,
                        modifier = Modifier.padding(start = (block.level * 12).dp, bottom = if (index == blocks.lastIndex) 0.dp else 4.dp),
                    )
                }

                is MarkdownBlock.NumberedItem -> {
                    Text(
                        text = buildAnnotatedString {
                            append("${block.number}. ")
                            append(block.text)
                        },
                        color = simpleText,
                        modifier = Modifier.padding(start = (block.level * 12).dp, bottom = if (index == blocks.lastIndex) 0.dp else 4.dp),
                    )
                }

                is MarkdownBlock.CodeFence -> {
                    CodeFenceBlock(
                        code = block.code,
                        modifier = Modifier.padding(bottom = if (index == blocks.lastIndex) 0.dp else 6.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun CodeFenceBlock(
    code: String,
    modifier: Modifier = Modifier,
) {
    val clipboardManager = LocalClipboardManager.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFF1E1E1E),
                shape = RoundedCornerShape(10.dp),
            )
            .padding(12.dp),
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clickable(onClick = {
                    clipboardManager.setText(AnnotatedString(code))
                })
                .size(20.dp),
            text = "\uD83D\uDCD1",
        )

        Text(
            text = remember(code) { buildHighlightedCodeString(code) },
            color = Color(0xFFE6E6E6),
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 28.dp),
            fontFamily = FontFamily.Monospace,
        )
    }
}

private fun buildHighlightedCodeString(code: String): AnnotatedString {
    val keywordColor = Color(0xFFCC7832)
    val annotationColor = Color(0xFFBBB529)
    val stringLiteralColor = Color(0xFF6A8759)
    val numberLiteralColor = Color(0xFF6897BB)
    val charLiteralColor = Color(0xFF008080)
    val regexLiteralColor = Color(0xFFF4AF3D)
    val commentColor = Color(0xFF808080) // Серый цвет для комментариев (IDEA Darcula)

    val kotlinKeywords = setOf(
        "as", "break", "class", "continue", "do", "else", "false", "for", "fun", "if", "in",
        "interface", "is", "null", "object", "package", "return", "super", "this", "throw", "true",
        "try", "typealias", "typeof", "val", "var", "when", "while", "by", "catch", "constructor",
        "delegate", "dynamic", "field", "file", "finally", "get", "import", "init", "param", "property",
        "receiver", "set", "setparam", "where", "actual", "abstract", "annotation", "companion", "const",
        "crossinline", "data", "enum", "expect", "external", "final", "infix", "inline", "inner", "internal",
        "lateinit", "noinline", "open", "operator", "out", "override", "private", "protected", "public",
        "reified", "sealed", "suspend", "tailrec", "value", "vararg"
    )

    val annotationRegex = Regex("@[A-Za-z_][A-Za-z0-9_]*")
    val tokenRegex = Regex("\\b[A-Za-z_][A-Za-z0-9_]*\\b")
    val stringLiteralRegex = Regex("\"(?:\\\\.|[^\\\"\\\\])*\"")
    val charLiteralRegex = Regex("'(?:\\\\.|[^'\\\\])'")
    val regexLiteralRegex = Regex("(?<![A-Za-z0-9_])/(?:\\\\.|[^/\\\\\n])+/[A-Za-z]*")
    val numberLiteralRegex = Regex("(?<![A-Za-z0-9_])(?:0[xX][0-9A-Fa-f]+(?:_[0-9A-Fa-f]+)*|0[bB][01]+(?:_[01]+)*|\\d+(?:_\\d+)*(?:\\.\\d+(?:_\\d+)*)?(?:[eE][+-]?\\d+(?:_\\d+)*)?[fFdDlL]?)(?![A-Za-z_])")
    val lineCommentRegex = Regex("//.*") // Находит // и все символы до конца строки

    data class HighlightMatch(
        val start: Int,
        val endExclusive: Int,
        val color: Color,
    )

    val literalMatches = buildList {
        stringLiteralRegex.findAll(code).forEach { match ->
            add(HighlightMatch(match.range.first, match.range.last + 1, stringLiteralColor))
        }
        charLiteralRegex.findAll(code).forEach { match ->
            add(HighlightMatch(match.range.first, match.range.last + 1, charLiteralColor))
        }
        annotationRegex.findAll(code).forEach { match ->
            add(HighlightMatch(match.range.first, match.range.last + 1, annotationColor))
        }
        regexLiteralRegex.findAll(code).forEach { match ->
            add(HighlightMatch(match.range.first, match.range.last + 1, regexLiteralColor))
        }
        numberLiteralRegex.findAll(code).forEach { match ->
            add(HighlightMatch(match.range.first, match.range.last + 1, numberLiteralColor))
        }
        lineCommentRegex.findAll(code).forEach { match ->
            add(HighlightMatch(match.range.first, match.range.last + 1, commentColor))
        }
    }

    return buildAnnotatedString {
        var currentIndex = 0

        val allMatches = (literalMatches + tokenRegex.findAll(code).mapNotNull { match ->
            val token = match.value
            if (token in kotlinKeywords) {
                HighlightMatch(match.range.first, match.range.last + 1, keywordColor)
            } else {
                null
            }
        }).sortedBy { it.start }

        for (match in allMatches) {
            if (match.start > currentIndex) {
                append(code.substring(currentIndex, match.start))
                currentIndex = match.start
            }

            if (match.start >= currentIndex) {
                withStyle(SpanStyle(color = match.color)) {
                    append(code.substring(match.start, match.endExclusive))
                }
                currentIndex = match.endExclusive
            }
        }

        if (currentIndex < code.length) {
            append(code.substring(currentIndex))
        }
    }
}

private sealed interface MarkdownBlock {
    data class Paragraph(val text: AnnotatedString) : MarkdownBlock
    data class Heading(val text: AnnotatedString, val level: Int) : MarkdownBlock
    data class BulletItem(val text: AnnotatedString, val level: Int) : MarkdownBlock
    data class NumberedItem(val number: Int, val text: AnnotatedString, val level: Int) : MarkdownBlock
    data class CodeFence(val code: String) : MarkdownBlock
}

private object MarkdownParser {
    private val headingRegex = Regex("^(#{1,3})\\s+(.*)$")
    private val bulletRegex = Regex("^(\\s*)([-*+])\\s+(.*)$")
    private val numberedRegex = Regex("^(\\s*)(\\d+)\\.\\s+(.*)$")

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
                if (inCodeFence) {
                    flushCodeFence()
                }
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

            val headingMatch = headingRegex.matchEntire(line)
            if (headingMatch != null) {
                flushParagraph()
                val level = headingMatch.groupValues[1].length
                val content = headingMatch.groupValues[2]
                blocks += MarkdownBlock.Heading(
                    text = parseInline(content),
                    level = level,
                )
                return@forEach
            }

            val bulletMatch = bulletRegex.matchEntire(rawLine)
            if (bulletMatch != null) {
                flushParagraph()
                val indent = bulletMatch.groupValues[1].length
                val content = bulletMatch.groupValues[3]
                blocks += MarkdownBlock.BulletItem(
                    text = parseInline(content),
                    level = indent / 2,
                )
                return@forEach
            }

            val numberedMatch = numberedRegex.matchEntire(rawLine)
            if (numberedMatch != null) {
                flushParagraph()
                val indent = numberedMatch.groupValues[1].length
                val number = numberedMatch.groupValues[2].toIntOrNull() ?: 1
                val content = numberedMatch.groupValues[3]
                blocks += MarkdownBlock.NumberedItem(
                    number = number,
                    text = parseInline(content),
                    level = indent / 2,
                )
                return@forEach
            }

            paragraphLines += rawLine
        }

        flushParagraph()
        if (inCodeFence) {
            flushCodeFence()
        }

        return blocks.ifEmpty { listOf(MarkdownBlock.Paragraph(AnnotatedString(""))) }
    }

    private fun parseInline(text: String): AnnotatedString {
        return buildAnnotatedString {
            var index = 0
            while (index < text.length) {
                when {
                    text.startsWith("**", index) -> {
                        val closing = text.indexOf("**", startIndex = index + 2)
                        if (closing > index + 2) {
                            pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                            append(parseInline(text.substring(index + 2, closing)))
                            pop()
                            index = closing + 2
                        } else {
                            append(text[index])
                            index += 1
                        }
                    }

                    text.startsWith("__", index) -> {
                        val closing = text.indexOf("__", startIndex = index + 2)
                        if (closing > index + 2) {
                            pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                            append(parseInline(text.substring(index + 2, closing)))
                            pop()
                            index = closing + 2
                        } else {
                            append(text[index])
                            index += 1
                        }
                    }

                    text.startsWith("*", index) -> {
                        val closing = text.indexOf('*', startIndex = index + 1)
                        if (closing > index + 1) {
                            pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                            append(parseInline(text.substring(index + 1, closing)))
                            pop()
                            index = closing + 1
                        } else {
                            append(text[index])
                            index += 1
                        }
                    }

                    text.startsWith("_", index) -> {
                        val closing = text.indexOf('_', startIndex = index + 1)
                        if (closing > index + 1) {
                            pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                            append(parseInline(text.substring(index + 1, closing)))
                            pop()
                            index = closing + 1
                        } else {
                            append(text[index])
                            index += 1
                        }
                    }

                    text.startsWith("`", index) -> {
                        val closing = text.indexOf('`', startIndex = index + 1)
                        if (closing > index + 1) {
                            pushStyle(
                                SpanStyle(
                                    fontFamily = FontFamily.Monospace,
                                    background = colorWithAlpha(0x33FFFFFF),
                                )
                            )
                            append(text.substring(index + 1, closing))
                            pop()
                            index = closing + 1
                        } else {
                            append(text[index])
                            index += 1
                        }
                    }

                    text.startsWith("~~", index) -> {
                        val closing = text.indexOf("~~", startIndex = index + 2)
                        if (closing > index + 2) {
                            pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
                            append(parseInline(text.substring(index + 2, closing)))
                            pop()
                            index = closing + 2
                        } else {
                            append(text[index])
                            index += 1
                        }
                    }

                    else -> {
                        append(text[index])
                        index += 1
                    }
                }
            }
        }
    }

    private fun colorWithAlpha(argb: Long): Color {
        return Color(argb)
    }
}
