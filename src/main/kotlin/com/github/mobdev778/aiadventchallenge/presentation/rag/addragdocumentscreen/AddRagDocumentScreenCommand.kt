package com.github.mobdev778.aiadventchallenge.presentation.rag.addragdocumentscreen

import com.github.mobdev778.aiadventchallenge.presentation.rag.addragdocumentscreen.model.AddRagDocumentScreenState.ChunkingStrategy

sealed interface AddRagDocumentScreenCommand {
    data object Back : AddRagDocumentScreenCommand
    data class OpenAddingDocument(
        val source: String,
        val title: String,
        val chunkingStrategy: ChunkingStrategy,
    ) : AddRagDocumentScreenCommand
}
