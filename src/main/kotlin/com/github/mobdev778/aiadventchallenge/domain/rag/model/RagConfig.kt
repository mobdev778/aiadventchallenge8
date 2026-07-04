package com.github.mobdev778.aiadventchallenge.domain.rag.model

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
