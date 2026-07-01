package com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen

import com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.model.RagDocumentListItem
import java.util.UUID

sealed interface RagDocumentListScreenEvent {
    data object OnBackClick : RagDocumentListScreenEvent
    data object OnAddClick : RagDocumentListScreenEvent
    data object OnRagConfigClick : RagDocumentListScreenEvent
    data class OnDocumentClick(val document: RagDocumentListItem) : RagDocumentListScreenEvent
    data class OnDeleteClick(val documentId: UUID) : RagDocumentListScreenEvent
}
