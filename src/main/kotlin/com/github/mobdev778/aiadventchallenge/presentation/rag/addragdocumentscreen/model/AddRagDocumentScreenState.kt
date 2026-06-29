package com.github.mobdev778.aiadventchallenge.presentation.rag.addragdocumentscreen.model

data class AddRagDocumentScreenState(
    val source: String = "",
    val title: String = "",
    val chunkingStrategy: ChunkingStrategy = ChunkingStrategy.FixedSize,
) {
    enum class ChunkingStrategy(val title: String) {
        FixedSize("Фиксированный размер"),
        Paragraphs("Абзацы"),
    }
}
