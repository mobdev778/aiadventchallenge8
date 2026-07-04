package com.github.mobdev778.aiadventchallenge.domain.mymcpserver.rag

import kotlinx.serialization.Serializable

@Serializable
data class MyMcpRagSearchChunkDto(
    val source: String,
    val section: Int,
    val text: String,
    val relevance_score: Double,
)
