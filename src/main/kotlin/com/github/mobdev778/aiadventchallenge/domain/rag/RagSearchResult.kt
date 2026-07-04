package com.github.mobdev778.aiadventchallenge.domain.rag

data class RagSearchResult(
    val source: String,
    val section: Int,
    val text: String,
    val vector: FloatArray,
    val score: Double // Добавляем оценку релевантности (например, Косинусное сходство от 0.0 до 1.0)
) : Comparable<RagSearchResult> {
    override fun compareTo(other: RagSearchResult): Int {
        return score.compareTo(other.score)
    }
}
