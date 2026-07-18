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

/**
 * Репозиторий для управления RAG-документами и их фрагментами.
 *
 * Выступает в роли промежуточного слоя между уровнями данных ([RagDocumentDao]) и предметной областью.
 * Отвечает за преобразование сущностей базы данных ([RagDocumentEntity], [RagDocumentChunkEntity])
 * в доменные модели ([RagDocument], [RagDocumentChunk]) и обратно, а также предоставляет
 * высокоуровневые операции над документами: создание, чтение, добавление фрагментов, удаление.
 * Использует Koin-аннотацию [Single] для предоставления единственного экземпляра в графе зависимостей.
 */
@Single
@Suppress("TooManyFunctions")
class RagDocumentRepository(
    private val ragDocumentDao: RagDocumentDao,
) {

    /**
     * Предоставляет реактивный поток, содержащий актуальный список всех документов.
     * Поток обновляется при любых изменениях в базе данных, автоматически конвертируя
     * сущности в доменные модели.
     *
     * @return [Flow] со списком [RagDocument], обновляемым в реальном времени.
     */
    fun observeDocuments(): Flow<List<RagDocument>> =
        ragDocumentDao.observeAll()
            .map { entities -> entities.map { it.toDomain() } }
            .distinctUntilChanged()

    /**
     * Создаёт новый документ в хранилище (или обновляет существующий при совпадении идентификатора).
     *
     * @param document Доменная модель документа, которую необходимо сохранить.
     */
    suspend fun createDocument(document: RagDocument) {
        ragDocumentDao.upsert(document.toEntity())
    }

    /**
     * Добавляет новый фрагмент (чанк) документа в базу данных.
     * При необходимости обновляет уже существующий фрагмент с тем же идентификатором.
     *
     * @param chunk Доменная модель фрагмента документа.
     */
    suspend fun add(chunk: RagDocumentChunk) {
        ragDocumentDao.upsertChunk(chunk.toEntity())
    }

    /**
     * Возвращает общее количество фрагментов, принадлежащих указанному документу.
     *
     * @param documentId Идентификатор документа, для которого подсчитываются чанки.
     * @return Количество чанков.
     */
    suspend fun getChunkCount(documentId: UUID): Int {
        return ragDocumentDao.getChunkCount(documentId.toString())
    }

    /**
     * Загружает страницу фрагментов документа с поддержкой пагинации.
     * Фрагменты упорядочены по номеру секции.
     *
     * @param documentId Идентификатор родительского документа.
     * @param limit Максимальное количество фрагментов на странице.
     * @param offset Смещение (количество пропускаемых фрагментов).
     * @return Список доменных моделей [RagDocumentChunk] для запрошенной страницы.
     */
    suspend fun getChunksPage(documentId: UUID, limit: Int, offset: Int): List<RagDocumentChunk> {
        return ragDocumentDao.getChunksPage(
            documentId = documentId.toString(),
            limit = limit,
            offset = offset,
        ).map { it.toDomain() }
    }

    /**
     * Удаляет документ по его идентификатору.
     *
     * @param documentId Идентификатор удаляемого документа.
     */
    suspend fun deleteDocument(documentId: UUID) {
        ragDocumentDao.deleteById(documentId.toString())
    }

    /**
     * Удаляет все фрагменты (чанки), связанные с указанным документом.
     *
     * @param documentId Идентификатор документа, для которого удаляются все фрагменты.
     */
    suspend fun deleteChunks(documentId: UUID) {
        ragDocumentDao.deleteChunks(documentId.toString())
    }

    /**
     * Удаляет конкретный фрагмент (чанк) по его идентификатору.
     *
     * @param id Уникальный идентификатор удаляемого фрагмента.
     */
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
