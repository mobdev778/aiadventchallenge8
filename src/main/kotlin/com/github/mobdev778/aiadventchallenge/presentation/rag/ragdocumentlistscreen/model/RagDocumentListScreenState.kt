package com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.model

import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagConfig

data class RagDocumentListScreenState(
    val ragConfig: RagConfig,
    val documents: List<RagDocumentListItem>,
)
