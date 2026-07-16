package com.github.mobdev778.aiadventchallenge.domain.rag.model

import java.util.UUID

/**
 * Фрагмент документа, хранящийся в RAG-системе вместе со своим векторным представлением.
 *
 * Каждый фрагмент соответствует части исходного документа и предназначен для семантического поиска
 * и генерации ответов на основе извлечённых данных. Связь с родительским документом устанавливается
 * через идентификатор [documentId].
 *
 * @property id Уникальный идентификатор фрагмента.
 * @property documentId Идентификатор родительского документа, к которому относится фрагмент.
 * @property section Номер раздела/фрагмента в исходном документе. Используется для восстановления порядка.
 * @property text Текстовое содержимое фрагмента.
 * @property vector Векторное представление текста, полученное от эмбеддинг-модели.
 */
data class RagDocumentChunk(
    val id: UUID,
    val documentId: UUID,
    val section: Int,
    val text: String,
    val vector: FloatArray,
)
