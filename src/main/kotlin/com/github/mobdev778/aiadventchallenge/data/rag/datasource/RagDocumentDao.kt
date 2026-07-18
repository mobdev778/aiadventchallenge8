package com.github.mobdev778.aiadventchallenge.data.rag.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.mobdev778.aiadventchallenge.data.rag.datasource.model.RagConfigEntity
import com.github.mobdev778.aiadventchallenge.data.rag.datasource.model.RagDocumentChunkEntity
import com.github.mobdev778.aiadventchallenge.data.rag.datasource.model.RagDocumentEntity
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс доступа к данным (DAO) для сущностей RAG-системы.
 *
 * Предоставляет методы для наблюдения, вставки и удаления документов ([RagDocumentEntity]),
 * их фрагментов ([RagDocumentChunkEntity]), а также управления конфигурацией ([RagConfigEntity]).
 * Все операции выполняются асинхронно (suspend или Flow) в контексте базы данных Room.
 */
@Dao
@Suppress("TooManyFunctions")
interface RagDocumentDao {

    /**
     * Возвращает наблюдаемый поток всех документов, отсортированных по заголовку и источнику
     * в алфавитном порядке без учёта регистра.
     *
     * @return [Flow] со списком [RagDocumentEntity], обновляющимся при изменениях в таблице.
     */
    @Query("SELECT * FROM rag_documents ORDER BY title COLLATE NOCASE ASC, source COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<RagDocumentEntity>>

    /**
     * Возвращает наблюдаемый поток конфигурации RAG по заданному идентификатору.
     * По умолчанию используется синглтонный идентификатор [RagConfigEntity.SINGLETON_ID].
     *
     * @param id Идентификатор записи конфигурации (по умолчанию синглтон).
     * @return [Flow], эмиттящий текущую конфигурацию или null, если она ещё не сохранена.
     */
    @Query("SELECT * FROM rag_config WHERE id = :id")
    fun observeConfig(id: Int = RagConfigEntity.SINGLETON_ID): Flow<RagConfigEntity?>

    /**
     * Приостанавливающая функция для однократного получения конфигурации RAG.
     *
     * @param id Идентификатор записи (по умолчанию [RagConfigEntity.SINGLETON_ID]).
     * @return Текущая конфигурация или null, если не найдена.
     */
    @Query("SELECT * FROM rag_config WHERE id = :id")
    suspend fun getConfig(id: Int = RagConfigEntity.SINGLETON_ID): RagConfigEntity?

    /**
     * Вставляет или заменяет запись документа в базе данных.
     * При конфликте по первичному ключу существующая запись будет перезаписана.
     *
     * @param entity Сущность документа для вставки или обновления.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: RagDocumentEntity)

    /**
     * Вставляет или заменяет фрагмент документа (чанк) в базе данных.
     *
     * @param entity Сущность фрагмента для вставки или обновления.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertChunk(entity: RagDocumentChunkEntity)

    /**
     * Вставляет или заменяет синглтонную конфигурацию RAG.
     * Ожидается, что entity имеет фиксированный идентификатор [RagConfigEntity.SINGLETON_ID].
     *
     * @param entity Сущность конфигурации.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertConfig(entity: RagConfigEntity)

    /**
     * Возвращает количество фрагментов, принадлежащих указанному документу.
     *
     * @param documentId Идентификатор родительского документа.
     * @return Количество чанков.
     */
    @Query("SELECT COUNT(*) FROM rag_document_chunks WHERE document_id = :documentId")
    suspend fun getChunkCount(documentId: String): Int

    /**
     * Возвращает страницу фрагментов заданного документа с поддержкой пагинации.
     * Фрагменты упорядочены по номеру секции [RagDocumentChunkEntity.section].
     *
     * @param documentId Идентификатор документа.
     * @param limit Максимальное количество фрагментов на странице.
     * @param offset Смещение (количество пропускаемых фрагментов).
     * @return Список [RagDocumentChunkEntity] для заданной страницы.
     */
    @Query(
        "SELECT * FROM rag_document_chunks " +
            "WHERE document_id = :documentId " +
            "ORDER BY section ASC LIMIT :limit OFFSET :offset"
    )
    suspend fun getChunksPage(documentId: String, limit: Int, offset: Int): List<RagDocumentChunkEntity>

    /**
     * Удаляет документ по его идентификатору.
     *
     * @param id Идентификатор документа.
     */
    @Query("DELETE FROM rag_documents WHERE id = :id")
    suspend fun deleteById(id: String)

    /**
     * Удаляет все фрагменты, связанные с указанным документом.
     *
     * @param documentId Идентификатор родительского документа.
     */
    @Query("DELETE FROM rag_document_chunks WHERE document_id = :documentId")
    suspend fun deleteChunks(documentId: String)

    /**
     * Удаляет конкретный фрагмент (чанк) по его идентификатору.
     *
     * @param id Идентификатор фрагмента.
     */
    @Query("DELETE FROM rag_document_chunks WHERE id = :id")
    suspend fun deleteChunkById(id: String)
}
