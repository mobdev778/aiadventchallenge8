package com.github.mobdev778.aiadventchallenge.domain.rag

data class RagSearchResult(
    val source: String,
    val section: Int,
    val text: String,
    val vector: FloatArray,
)