package com.github.mobdev778.aiadventchallenge.domain.rag.ranker

interface Ranker {

    suspend fun init(query: String)

    suspend fun rank(found: String, vector: FloatArray): Double
}
