package com.github.mobdev778.aiadventchallenge.presentation.rag.addragdocumentscreen

sealed interface AddRagDocumentScreenEvent {
    data object OnBackClick : AddRagDocumentScreenEvent
    data object OnChooseSourceClick : AddRagDocumentScreenEvent
    data class OnSourceChange(val value: String) : AddRagDocumentScreenEvent
    data class OnTitleChange(val value: String) : AddRagDocumentScreenEvent
    data class OnChunkingStrategyChange(val index: Int) : AddRagDocumentScreenEvent
    data object OnAddClick : AddRagDocumentScreenEvent
}
