package com.github.mobdev778.aiadventchallenge.domain.rag.ranker

import dev.langchain4j.model.scoring.onnx.OnnxScoringModel

/**
 * Реализация [Ranker], использующая модель ONNX для оценки релевантности.
 *
 * В качестве бэкенда применяется [OnnxScoringModel], которая на этапе [rank] вычисляет
 * семантическую близость между исходным запросом и найденным фрагментом. Входной вектор
 * [vector] в данной реализации игнорируется, так как модель самостоятельно кодирует текст
 * фрагмента.
 *
 * @param scoringModel предобученная ONNX-модель для рейтингования
 */
class ReRanker(
    private val scoringModel: OnnxScoringModel,
) : Ranker {

    /**
     * Сохранённый текст запроса пользователя.
     *
     * Устанавливается в методе [init] и используется в каждом вызове [rank] для
     * сопоставления с фрагментом.
     */
    private var query: String = ""

    /**
     * Инициализирует ранкер запросом пользователя.
     *
     * @param query текстовый запрос, относительно которого будет оцениваться
     *              релевантность найденных фрагментов
     */
    override suspend fun init(query: String) {
        this.query = query
    }

    /**
     * Вычисляет оценку релевантности найденного фрагмента документа.
     *
     * Оценка вычисляется с помощью ONNX-модели путём скоринга пары (запрос, текст фрагмента).
     * Параметр `vector` в данной реализации не используется.
     *
     * @param found текст найденного фрагмента документа-кандидата
     * @param vector векторное представление (эмбеддинг) данного фрагмента (игнорируется)
     * @return числовая оценка релевантности фрагмента, вычисленная моделью
     */
    override suspend fun rank(found: String, vector: FloatArray): Double {
        return scoringModel.score(query, found).content()
    }
}
