package com.github.mobdev778.aiadventchallenge.domain.mymcpserver.rag

import kotlinx.serialization.Serializable

@Serializable
data class MyMcpRagSearchResponseDto(
    val status: String,
    val message: String? = null,
    val chunks: List<MyMcpRagSearchChunkDto> = emptyList(),
)

