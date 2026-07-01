package com.github.mobdev778.aiadventchallenge.domain.rag

import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagDocumentChunk
import dev.langchain4j.model.embedding.EmbeddingModel
import java.util.UUID

class RagChunkGenerator(
    private val documentId: UUID,
    private val embeddingModel: EmbeddingModel,
) {

    fun generate(section: Int, text: String): RagDocumentChunk {
        val response = embeddingModel.embed(text)
        val vector = response.content().vector()

        return RagDocumentChunk(
            id = UUID.randomUUID(),
            documentId = documentId,
            section = section,
            text = text,
            vector = vector,
        )
    }
}
