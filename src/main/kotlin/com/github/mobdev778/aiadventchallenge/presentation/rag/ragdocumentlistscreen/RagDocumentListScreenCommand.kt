package com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen

import com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.model.RagDocumentListItem

sealed interface RagDocumentListScreenCommand {
    data object Back : RagDocumentListScreenCommand
    data object OpenAddDocument : RagDocumentListScreenCommand
    data object OpenRagConfig : RagDocumentListScreenCommand
    data class OpenDocument(val document: RagDocumentListItem) : RagDocumentListScreenCommand
}
