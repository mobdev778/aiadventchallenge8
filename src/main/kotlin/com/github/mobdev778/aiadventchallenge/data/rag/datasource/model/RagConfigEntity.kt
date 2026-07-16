package com.github.mobdev778.aiadventchallenge.data.rag.datasource.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сущность базы данных, представляющая таблицу `rag_config`.
 * Хранит конфигурацию Retrieval-Augmented Generation (RAG), включая параметры поиска, фильтрации,
 * ранжирования и пути к моделям. Реализована как синглтонная запись с фиксированным идентификатором [SINGLETON_ID].
 */
@Entity(tableName = "rag_config")
data class RagConfigEntity(
    /**
     * Уникальный идентификатор записи. Используется константа [SINGLETON_ID] для обеспечения синглтонности.
     */
    @PrimaryKey
    val id: Int = SINGLETON_ID,

    /**
     * Количество документов, получаемых на этапе первичного поиска (до фильтрации).
     */
    val topKBefore: Int,

    /**
     * Тип применяемой фильтрации (например, по длине или по ключевым словам).
     */
    val filterType: String,

    /**
     * Флаг, указывающий, нужно ли переписывать исходный запрос перед использованием.
     */
    val useQueryRewriting: Boolean,

    /**
     * Количество документов, оставляемых после этапа фильтрации и ранжирования.
     */
    val topKAfter: Int,

    /**
     * Флаг, указывающий, применять ли порог минимального сходства.
     */
    val useMinSimilarity: Boolean,

    /**
     * Пороговое значение косинусного сходства для фильтрации кандидатов.
     */
    val minSimilarity: Double,

    /**
     * Путь к файлу модели ранжирования (например, .onnx).
     */
    val rankerModelPath: String,

    /**
     * Путь к файлу токенизатора для модели ранжирования.
     */
    val rankerTokenizerPath: String,

    /**
     * Путь к файлу модели эмбеддингов.
     */
    val embModelPath: String,

    /**
     * Путь к файлу токенизатора для модели эмбеддингов.
     */
    val embTokenizerPath: String,
) {
    companion object {
        /**
         * Константа, задающая идентификатор единственной записи в таблице.
         * Таблица `rag_config` хранит только одну строку конфигурации RAG.
         */
        const val SINGLETON_ID: Int = 1
    }
}
