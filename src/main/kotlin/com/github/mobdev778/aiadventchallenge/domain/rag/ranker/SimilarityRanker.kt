package com.github.mobdev778.aiadventchallenge.domain.rag.ranker

import com.github.mobdev778.aiadventchallenge.domain.rag.RagChunkGenerator

/**
 * Ранкер, оценивающий релевантность фрагментов документа запросу посредством косинусного сходства.
 *
 * При инициализации ([init]) получает эмбеддинг запроса через предоставленный [RagChunkGenerator].
 * Оценка для каждого фрагмента вычисляется как косинусное сходство между вектором запроса и вектором фрагмента
 * (расширение [FloatArray.cosineSimilarity]). Если инициализация не была выполнена, оценка
 * возвращает [Double.NEGATIVE_INFINITY].
 *
 * @property ragChunkGenerator генератор чанков, используемый для получения векторного представления запроса.
 */
class SimilarityRanker(
    val ragChunkGenerator: RagChunkGenerator,
) : Ranker {

    private var queryVector: FloatArray? = null

    /**
     * Инициализирует ранкер, вычисляя вектор запроса через [RagChunkGenerator].
     *
     * Создаётся виртуальный чанк с секцией `0`, эмбеддинг которого сохраняется как вектор запроса.
     *
     * @param query текстовый запрос пользователя, для которого будет оцениваться релевантность фрагментов.
     */
    override suspend fun init(query: String) {
        queryVector = ragChunkGenerator
            .generate(section = 0, text = query)
            .vector
    }

    /**
     * Возвращает оценку релевантности фрагмента запросу как косинусное сходство векторов.
     *
     * Если ранкер не был инициализирован (вектор запроса отсутствует), возвращается [Double.NEGATIVE_INFINITY].
     *
     * @param found текст найденного фрагмента (не используется в вычислении, принят для совместимости с интерфейсом).
     * @param vector векторное представление фрагмента, с которым вычисляется сходство.
     * @return значение косинусного сходства в интервале `[-1.0, 1.0]` или [Double.NEGATIVE_INFINITY] при ошибке.
     */
    override suspend fun rank(found: String, vector: FloatArray): Double {
        val qVector = queryVector ?: return Double.NEGATIVE_INFINITY
        return qVector.cosineSimilarity(vector)
    }
}
