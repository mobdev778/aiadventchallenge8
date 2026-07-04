package com.github.mobdev778.aiadventchallenge.domain.rag.model

import java.util.UUID

data class RagDocument(
    val id: UUID,
    val source: String,
    val title: String,
)
