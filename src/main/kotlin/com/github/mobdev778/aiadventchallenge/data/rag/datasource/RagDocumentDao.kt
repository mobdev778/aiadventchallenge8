package com.github.mobdev778.aiadventchallenge.data.rag.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.mobdev778.aiadventchallenge.data.rag.datasource.model.RagConfigEntity
import com.github.mobdev778.aiadventchallenge.data.rag.datasource.model.RagDocumentChunkEntity
import com.github.mobdev778.aiadventchallenge.data.rag.datasource.model.RagDocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RagDocumentDao {

    @Query("SELECT * FROM rag_documents ORDER BY title COLLATE NOCASE ASC, source COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<RagDocumentEntity>>

    @Query("SELECT * FROM rag_config WHERE id = :id")
    fun observeConfig(id: Int = RagConfigEntity.SINGLETON_ID): Flow<RagConfigEntity?>

    @Query("SELECT * FROM rag_config WHERE id = :id")
    suspend fun getConfig(id: Int = RagConfigEntity.SINGLETON_ID): RagConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: RagDocumentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertChunk(entity: RagDocumentChunkEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertConfig(entity: RagConfigEntity)

    @Query("SELECT COUNT(*) FROM rag_document_chunks WHERE document_id = :documentId")
    suspend fun getChunkCount(documentId: String): Int

    @Query(
        "SELECT * FROM rag_document_chunks " +
            "WHERE document_id = :documentId " +
            "ORDER BY section ASC LIMIT :limit OFFSET :offset"
    )
    suspend fun getChunksPage(documentId: String, limit: Int, offset: Int): List<RagDocumentChunkEntity>

    @Query("DELETE FROM rag_documents WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM rag_document_chunks WHERE document_id = :documentId")
    suspend fun deleteChunks(documentId: String)

    @Query("DELETE FROM rag_document_chunks WHERE id = :id")
    suspend fun deleteChunkById(id: String)
}
