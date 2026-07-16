package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.composable

import androidx.compose.ui.text.AnnotatedString

/**
 * Иерархия блоков разметки Markdown, используемая для отображения сообщений в чате.
 * Каждый потомок описывает определённый структурный элемент сообщения
 * с соответствующими стилями и вложенностью.
 */
sealed interface MarkdownBlock {
    /**
     * Обычный текстовый абзац.
     *
     * @param text стилизованный текст абзаца.
     */
    data class Paragraph(val text: AnnotatedString) : MarkdownBlock

    /**
     * Заголовок.
     *
     * @param text стилизованный текст заголовка.
     * @param level уровень заголовка (1–6, как в HTML).
     */
    data class Heading(val text: AnnotatedString, val level: Int) : MarkdownBlock

    /**
     * Элемент маркированного (ненумерованного) списка.
     *
     * @param text стилизованный текст элемента списка.
     * @param level уровень вложенности (0 — верхний уровень).
     */
    data class BulletItem(val text: AnnotatedString, val level: Int) : MarkdownBlock

    /**
     * Элемент нумерованного списка.
     *
     * @param number порядковый номер элемента в списке.
     * @param text стилизованный текст элемента списка.
     * @param level уровень вложенности (0 — верхний уровень).
     */
    data class NumberedItem(val number: Int, val text: AnnotatedString, val level: Int) : MarkdownBlock

    /**
     * Блок кода, выделенный ограждением (```).
     *
     * @param code исходный код блока как обычная строка (без оформления).
     */
    data class CodeFence(val code: String) : MarkdownBlock
}
