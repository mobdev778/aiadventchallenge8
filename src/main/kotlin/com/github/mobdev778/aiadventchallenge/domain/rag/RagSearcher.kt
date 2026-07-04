package com.github.mobdev778.aiadventchallenge.domain.rag

interface RagSearcher {

    suspend fun search(query: String): List<RagSearchResult>
}
