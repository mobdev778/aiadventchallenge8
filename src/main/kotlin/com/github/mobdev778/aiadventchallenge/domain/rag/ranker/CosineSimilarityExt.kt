package com.github.mobdev778.aiadventchallenge.domain.rag.ranker

import kotlin.math.sqrt

/**
 * Вычисляет косинусное сходство между двумя векторами, представленными массивами чисел с плавающей точкой.
 *
 * Функция является расширением для [FloatArray] и сравнивает текущий массив (this) с переданным [other].
 * Если массивы пусты, имеют разный размер или одна из норм равна нулю, возвращается [Double.NEGATIVE_INFINITY],
 * сигнализируя о невозможности вычислить корректное значение.
 *
 * @param other Второй массив, с которым вычисляется косинусное сходство.
 * @return Значение косинусного сходства в интервале `[-1.0, 1.0]` или [Double.NEGATIVE_INFINITY] в особых случаях.
 */
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

    val result = if (leftNorm == 0.0 || rightNorm == 0.0) {
        Double.NEGATIVE_INFINITY
    } else {
        dot / (sqrt(leftNorm) * sqrt(rightNorm))
    }
    return result
}
