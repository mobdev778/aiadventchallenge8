package com.github.mobdev778.aiadventchallenge.domain.rag.queryrewriter

import kotlinx.serialization.Serializable

@Serializable
data class RewrittenQueriesResponse(
    val rewritten_queries: List<String>
)