package com.github.mobdev778.aiadventchallenge.domain.rag

import java.util.UUID

interface RagSearcher {

    suspend fun search(documentId: UUID, query: String, maxResults: Int): List<RagSearchResult>
}
