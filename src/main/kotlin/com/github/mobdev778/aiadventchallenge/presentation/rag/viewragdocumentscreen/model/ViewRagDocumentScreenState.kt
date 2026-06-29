package com.github.mobdev778.aiadventchallenge.presentation.rag.viewragdocumentscreen.model

import com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.model.RagDocumentListItem

data class ViewRagDocumentScreenState(
    val document: RagDocumentListItem? = null,
    val query: String = "",
    val isSearching: Boolean = false,
    val results: List<ViewRagDocumentSearchResult> = emptyList(),
)

data class ViewRagDocumentSearchResult(
    val source: String,
    val section: Int,
    val text: String,
)
