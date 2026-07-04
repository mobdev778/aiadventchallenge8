package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.composable

import androidx.compose.ui.text.AnnotatedString

sealed interface MarkdownBlock {
    data class Paragraph(val text: AnnotatedString) : MarkdownBlock
    data class Heading(val text: AnnotatedString, val level: Int) : MarkdownBlock
    data class BulletItem(val text: AnnotatedString, val level: Int) : MarkdownBlock
    data class NumberedItem(val number: Int, val text: AnnotatedString, val level: Int) : MarkdownBlock
    data class CodeFence(val code: String) : MarkdownBlock
}
