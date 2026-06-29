package com.github.mobdev778.aiadventchallenge.domain.rag.model

import java.util.UUID

data class RagDocumentChunk(
    val id: UUID,
    val documentId: UUID,
    val section: Int,
    val text: String,
    val vector: FloatArray,
)