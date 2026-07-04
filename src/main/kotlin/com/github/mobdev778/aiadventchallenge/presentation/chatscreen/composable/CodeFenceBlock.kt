package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import org.jetbrains.jewel.ui.component.Text

@Suppress("MagicNumber")
private val CodeFenceBackgroundColor = Color(0xFF1E1E1E)

@Suppress("MagicNumber")
private val CodeFenceTextColor = Color(0xFFE6E6E6)

@Composable
fun CodeFenceBlock(
    code: String,
    modifier: Modifier = Modifier.Companion,
) {
    val clipboardManager = LocalClipboardManager.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = CodeFenceBackgroundColor,
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
            color = CodeFenceTextColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 28.dp),
            fontFamily = FontFamily.Monospace,
        )
    }
}

@Suppress("MagicNumber")
private fun buildHighlightedCodeString(code: String): AnnotatedString {
    val literalMatches = collectLiteralMatches(code)
    val kotlinKeywords = kotlinKeywords()
    val tokenRegex = Regex("\\b[A-Za-z_][A-Za-z0-9_]*\\b")

    return buildAnnotatedString {
        var currentIndex = 0

        val allMatches = (literalMatches + tokenRegex.findAll(code).mapNotNull { match ->
            val token = match.value
            if (token in kotlinKeywords) {
                HighlightMatch(match.range.first, match.range.last + 1, keywordColor())
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

@Suppress("MagicNumber")
private fun keywordColor() = Color(0xFFCC7832)

@Suppress("MagicNumber")
private fun kotlinKeywords(): Set<String> = setOf(
    "as", "break", "class", "continue", "do", "else", "false", "for", "fun", "if", "in",
    "interface", "is", "null", "object", "package", "return", "super", "this", "throw", "true",
    "try", "typealias", "typeof", "val", "var", "when", "while", "by", "catch", "constructor",
    "delegate", "dynamic", "field", "file", "finally", "get", "import", "init", "param", "property",
    "receiver", "set", "setparam", "where", "actual", "abstract", "annotation", "companion", "const",
    "crossinline", "data", "enum", "expect", "external", "final", "infix", "inline", "inner", "internal",
    "lateinit", "noinline", "open", "operator", "out", "override", "private", "protected", "public",
    "reified", "sealed", "suspend", "tailrec", "value", "vararg",
)

@Suppress("MagicNumber")
private fun collectLiteralMatches(code: String): List<HighlightMatch> {
    val stringLiteralColor = Color(0xFF6A8759)
    val charLiteralColor = Color(0xFF008080)
    val annotationColor = Color(0xFFBBB529)
    val regexLiteralColor = Color(0xFFF4AF3D)
    val numberLiteralColor = Color(0xFF6897BB)
    val commentColor = Color(0xFF808080)

    val annotationRegex = Regex("@[A-Za-z_][A-Za-z0-9_]*")
    val stringLiteralRegex = Regex("\"(?:\\\\.|[^\\\"\\\\])*\"")
    val charLiteralRegex = Regex("'(?:\\\\.|[^'\\\\])'")
    val regexLiteralRegex = Regex("(?<![A-Za-z0-9_])/(?:\\\\.|[^/\\\\\n])+/[A-Za-z]*")
    val numberLiteralRegex = Regex(
        "(?<![A-Za-z0-9_])(?:0[xX][0-9A-Fa-f]+(?:_[0-9A-Fa-f]+)*" +
                "|0[bB][01]+(?:_[01]+)*|\\d+(?:_\\d+)*(?:\\.\\d+(?:_\\d+)*)?" +
                "(?:[eE][+-]?\\d+(?:_\\d+)*)?[fFdDlL]?)(?![A-Za-z_])"
    )
    val lineCommentRegex = Regex("//.*")

    return buildList {
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
}

private data class HighlightMatch(
    val start: Int,
    val endExclusive: Int,
    val color: Color,
)
