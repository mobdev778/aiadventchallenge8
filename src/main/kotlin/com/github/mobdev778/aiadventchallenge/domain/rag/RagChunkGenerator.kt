package com.github.mobdev778.aiadventchallenge.domain.rag

import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagDocumentChunk
import dev.langchain4j.model.embedding.EmbeddingModel
import dev.langchain4j.model.openai.OpenAiEmbeddingModel
import java.time.Duration
import java.util.UUID

class RagChunkGenerator(
    private val documentId: UUID,
) {

    val embeddingModel: EmbeddingModel = OpenAiEmbeddingModel.builder()
        .baseUrl("http://localhost:1234/v1")
        .apiKey("lm-studio")
        .modelName("text-embedding-nomic-embed-text-v1.5")
        .timeout(Duration.ofSeconds(60))
        .logRequests(true)
        .logResponses(true)
        .build()

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
