package com.github.mobdev778.aiadventchallenge.domain.rag

import java.util.UUID

/**
 * Интерфейс для выполнения поиска релевантных фрагментов в документе с использованием RAG (Retrieval-Augmented Generation) подхода.
 *
 * Реализации этого интерфейса обеспечивают поиск семантически близких фрагментов текста по заданному запросу
 * в пределах указанного документа. Результаты представляются объектами [RagSearchResult].
 */
interface RagSearcher {

    /**
     * Выполняет поиск релевантных фрагментов в документе по заданному запросу.
     *
     * @param documentId уникальный идентификатор документа, в котором производится поиск.
     * @param query текстовый запрос, для которого ищутся релевантные фрагменты.
     * @param maxResults максимальное количество возвращаемых результатов поиска.
     * @return список объектов [RagSearchResult], содержащих информацию о релевантных фрагментах.
     */
    suspend fun search(documentId: UUID, query: String, maxResults: Int): List<RagSearchResult>
}
