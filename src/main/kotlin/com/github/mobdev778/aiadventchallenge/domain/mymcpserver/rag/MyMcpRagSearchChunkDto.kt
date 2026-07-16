package com.github.mobdev778.aiadventchallenge.domain.mymcpserver.rag

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO, представляющий фрагмент (чанк) результатов поиска в RAG-системе.
 *
 * Используется для передачи данных от сервера поиска релевантных фрагментов текста.
 * Каждый чанк содержит информацию об источнике, секции, тексте и оценке релевантности.
 *
 * @property source Идентификатор или название источника документа.
 * @property section Номер или индекс секции (раздела) в документе.
 * @property text Текстовое содержимое найденного фрагмента.
 * @property relevanceScore Оценка релевантности данного фрагмента поисковому запросу (от 0 до 1).
 */
@Serializable
data class MyMcpRagSearchChunkDto(
    val source: String,
    val section: Int,
    val text: String,
    @SerialName("relevance_score")
    val relevanceScore: Double,
)
