package com.github.mobdev778.aiadventchallenge.presentation.rag.addragdocumentscreen.model

/**
 * Состояние экрана добавления документа в RAG (Retrieval-Augmented Generation).
 *
 * Содержит данные, введённые пользователем: источник документа, заголовок и выбранную стратегию
 * разбиения текста на чанки для последующей индексации и поиска.
 *
 * @property source Источник документа (URL, путь к файлу или иной идентификатор).
 * @property title Заголовок документа, задаваемый пользователем.
 * @property chunkingStrategy Стратегия разбиения текста документа на чанки.
 *   По умолчанию — [ChunkingStrategy.FixedSize].
 */
data class AddRagDocumentScreenState(
    val source: String = "",
    val title: String = "",
    val chunkingStrategy: ChunkingStrategy = ChunkingStrategy.FixedSize,
) {
    /**
     * Стратегия разбиения текста документа на чанки (фрагменты).
     *
     * @property title Человекочитаемое название стратегии на русском языке.
     */
    enum class ChunkingStrategy(val title: String) {
        /** Разбиение на чанки фиксированного размера. */
        FixedSize("Фиксированный размер"),
        /** Разбиение на чанки по абзацам. */
        Paragraphs("Абзацы"),
    }
}
