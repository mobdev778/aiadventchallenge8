package com.github.mobdev778.aiadventchallenge.domain.rag.queryrewriter

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RewrittenQueriesResponse(
    @SerialName("rewritten_queries")
    val rewrittenQueries: List<String>
)
