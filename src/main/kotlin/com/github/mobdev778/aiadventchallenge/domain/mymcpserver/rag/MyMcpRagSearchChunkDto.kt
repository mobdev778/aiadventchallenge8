package com.github.mobdev778.aiadventchallenge.domain.mymcpserver.rag

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyMcpRagSearchChunkDto(
    val source: String,
    val section: Int,
    val text: String,
    @SerialName("relevance_score")
    val relevanceScore: Double,
)
