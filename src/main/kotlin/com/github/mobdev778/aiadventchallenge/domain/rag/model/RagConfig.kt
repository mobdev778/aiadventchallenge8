package com.github.mobdev778.aiadventchallenge.domain.rag.model

/**
 * Конфигурация модуля RAG (Retrieval-Augmented Generation), определяющая
 * параметры фильтрации и предобработки релевантных документов перед
 * передачей в генеративную модель.
 *
 * Объединяет настройки двухэтапного отбора: первичный поиск (top-K)
 * и последующую фильтрацию с возможным переранжированием. Позволяет
 * гибко комбинировать эвристические, векторные и модельные подходы
 * для повышения качества итоговых контекстов.
 *
 * @property topKBefore Количество документов, извлекаемых на первом этапе поиска.
 * @property filterType Тип фильтрации, применяемой ко множеству документов.
 *           См. [RagFilterType].
 * @property useQueryRewriting Флаг, разрешающий переформулировку исходного запроса
 *           перед поиском для улучшения покрытия.
 * @property topKAfter Количество документов, оставляемых после фильтрации и/или
 *           переранжирования.
 * @property useMinSimilarity Определяет, используется ли минимальный порог сходства
 *           для отсева слабо релевантных документов.
 * @property minSimilarity Значение минимального допустимого сходства (например,
 *           косинусного), применяемое, если [useMinSimilarity] == true.
 * @property rankerModelPath Путь к файлу модели-реранкера, используемой при
 *           типе фильтрации [RagFilterType.Reranker].
 * @property rankerTokenizerPath Путь к файлу токенизатора для модели-реранкера.
 * @property embModelPath Путь к файлу модели эмбеддингов.
 * @property embTokenizerPath Путь к файлу токенизатора модели эмбеддингов.
 */
data class RagConfig(
    val topKBefore: Int,
    val filterType: RagFilterType,
    val useQueryRewriting: Boolean,
    val topKAfter: Int,
    val useMinSimilarity: Boolean,
    val minSimilarity: Double,
    val rankerModelPath: String,
    val rankerTokenizerPath: String,
    val embModelPath: String,
    val embTokenizerPath: String,
)
