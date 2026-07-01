package com.github.mobdev778.aiadventchallenge.domain.rag.ranker

import kotlin.math.sqrt

fun FloatArray.cosineSimilarity(other: FloatArray): Double {
    if (isEmpty() || other.isEmpty() || size != other.size) return Double.NEGATIVE_INFINITY

    var dot = 0.0
    var leftNorm = 0.0
    var rightNorm = 0.0

    for (index in indices) {
        val l = this[index].toDouble()
        val r = other[index].toDouble()
        dot += l * r
        leftNorm += l * l
        rightNorm += r * r
    }

    if (leftNorm == 0.0 || rightNorm == 0.0) return Double.NEGATIVE_INFINITY

    return dot / (sqrt(leftNorm) * sqrt(rightNorm))
}