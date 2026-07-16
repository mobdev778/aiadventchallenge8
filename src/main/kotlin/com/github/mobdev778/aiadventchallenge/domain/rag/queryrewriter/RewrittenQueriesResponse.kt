package com.github.mobdev778.aiadventchallenge.domain.rag.queryrewriter

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO-объект, представляющий ответ от сервиса перезаписи запросов (Query Rewriter)
 * в рамках RAG-пайплайна (Retrieval-Augmented Generation).
 *
 * Содержит список альтернативных, переформулированных версий исходного пользовательского
 * запроса, которые используются для повышения качества и полноты поиска релевантных
 * документов в векторном хранилище.
 *
 * @property rewrittenQueries Список переписанных (переформулированных) вариантов
 *   исходного запроса. Каждый элемент — альтернативная формулировка, позволяющая
 *   охватить различные аспекты исходного вопроса и улучшить результаты семантического
 *   поиска.
 */
@Serializable
data class RewrittenQueriesResponse(
    @SerialName("rewritten_queries")
    val rewrittenQueries: List<String>
)
