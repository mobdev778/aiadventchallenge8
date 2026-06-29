package com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.model

import java.util.UUID

data class RagDocumentListItem(
    val id: UUID,
    val source: String,
    val title: String,
    val chunkCount: Int,
)
