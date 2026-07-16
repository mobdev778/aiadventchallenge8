package com.github.mobdev778.aiadventchallenge.domain.rag

/**
 * Результат поиска в RAG-системе (Retrieval-Augmented Generation).
 *
 * Представляет один найденный фрагмент текста вместе с вектором и оценкой релевантности.
 * Реализует [Comparable] для естественной сортировки по убыванию релевантности (поле [score]).
 *
 * @property source идентификатор источника или документа, откуда взят фрагмент.
 * @property section номер секции или логического блока внутри источника.
 * @property text текстовое содержимое найденного фрагмента.
 * @property vector векторное представление текста (эмбеддинг) для семантических операций.
 * @property score оценка релевантности запросу (например, косинусное сходство) в диапазоне от 0.0 до 1.0.
 */
data class RagSearchResult(
    val source: String,
    val section: Int,
    val text: String,
    val vector: FloatArray,
    val score: Double // Добавляем оценку релевантности (например, Косинусное сходство от 0.0 до 1.0)
) : Comparable<RagSearchResult> {

    /**
     * Сравнивает текущий результат с [other] по значению оценки релевантности.
     *
     * @param other другой результат поиска.
     * @return отрицательное число, ноль или положительное число, если
     *         текущая оценка меньше, равна или больше оценки [other] соответственно.
     */
    override fun compareTo(other: RagSearchResult): Int {
        return score.compareTo(other.score)
    }
}
