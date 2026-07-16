package com.github.mobdev778.aiadventchallenge.domain.rag

import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagDocumentChunk
import dev.langchain4j.model.embedding.EmbeddingModel
import java.util.UUID

/**
 * Генератор чанков для RAG-индексации.
 * Создаёт [RagDocumentChunk] с векторным представлением текста,
 * полученным с помощью переданной [embeddingModel].
 *
 * @property documentId идентификатор документа, к которому относятся все порождаемые чанки
 * @property embeddingModel модель эмбеддингов, преобразующая текст в числовой вектор
 */
class RagChunkGenerator(
    private val documentId: UUID,
    private val embeddingModel: EmbeddingModel,
) {

    /**
     * Генерирует новый чанк документа с эмбеддингом для указанной секции.
     *
     * @param section номер секции внутри документа
     * @param text текстовое содержимое секции
     * @return готовый объект [RagDocumentChunk] с уникальным идентификатором, текстом и вектором
     */
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
