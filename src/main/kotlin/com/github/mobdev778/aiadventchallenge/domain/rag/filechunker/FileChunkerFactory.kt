package com.github.mobdev778.aiadventchallenge.domain.rag.filechunker

import com.github.mobdev778.aiadventchallenge.presentation.rag.addragdocumentscreen.model.AddRagDocumentScreenState
import java.io.File

class FileChunkerFactory {

    fun create(
        file: File,
        strategy: AddRagDocumentScreenState.ChunkingStrategy
    ): FileChunker {
        return when (strategy) {
            AddRagDocumentScreenState.ChunkingStrategy.FixedSize -> FixedSizeFileChunker(file)
            AddRagDocumentScreenState.ChunkingStrategy.Paragraphs -> ParagraphsFileChunker(file)
        }
    }
}
