package com.github.mobdev778.aiadventchallenge.data.rag.repository

import com.github.mobdev778.aiadventchallenge.data.rag.datasource.RagDocumentDao
import com.github.mobdev778.aiadventchallenge.data.rag.datasource.model.RagDocumentChunkEntity
import com.github.mobdev778.aiadventchallenge.data.rag.datasource.model.RagDocumentEntity
import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagDocument
import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagDocumentChunk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single
import java.util.UUID

@Single
class RagDocumentRepository(
    private val ragDocumentDao: RagDocumentDao,
) {

    fun observeDocuments(): Flow<List<RagDocument>> =
        ragDocumentDao.observeAll()
            .map { entities -> entities.map { it.toDomain() } }
            .distinctUntilChanged()

    suspend fun createDocument(document: RagDocument) {
        ragDocumentDao.upsert(document.toEntity())
    }

    suspend fun add(chunk: RagDocumentChunk) {
        ragDocumentDao.upsertChunk(chunk.toEntity())
    }

    suspend fun getChunkCount(documentId: UUID): Int {
        return ragDocumentDao.getChunkCount(documentId.toString())
    }

    suspend fun getChunksPage(documentId: UUID, limit: Int, offset: Int): List<RagDocumentChunk> {
        return ragDocumentDao.getChunksPage(
            documentId = documentId.toString(),
            limit = limit,
            offset = offset,
        ).map { it.toDomain() }
    }

    suspend fun deleteDocument(documentId: UUID) {
        ragDocumentDao.deleteById(documentId.toString())
    }

    suspend fun deleteChunks(documentId: UUID) {
        ragDocumentDao.deleteChunks(documentId.toString())
    }

    suspend fun deleteChunk(id: UUID) {
        ragDocumentDao.deleteChunkById(id.toString())
    }

    private fun RagDocumentEntity.toDomain(): RagDocument =
        RagDocument(
            id = UUID.fromString(id),
            source = source,
            title = title,
        )

    private fun RagDocument.toEntity(): RagDocumentEntity =
        RagDocumentEntity(
            id = id.toString(),
            source = source,
            title = title,
        )

    private fun RagDocumentChunk.toEntity(): RagDocumentChunkEntity =
        RagDocumentChunkEntity(
            id = id.toString(),
            documentId = documentId.toString(),
            section = section,
            text = text,
            vector = vector.joinToString(separator = ","),
        )

    private fun RagDocumentChunkEntity.toDomain(): RagDocumentChunk =
        RagDocumentChunk(
            id = UUID.fromString(id),
            documentId = UUID.fromString(documentId),
            section = section,
            text = text,
            vector = vector
                .split(',')
                .filter { it.isNotBlank() }
                .map { it.toFloat() }
                .toFloatArray(),
        )
}
