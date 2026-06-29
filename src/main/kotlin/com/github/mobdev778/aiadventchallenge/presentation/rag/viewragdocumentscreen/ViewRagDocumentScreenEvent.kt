package com.github.mobdev778.aiadventchallenge.presentation.rag.viewragdocumentscreen

sealed interface ViewRagDocumentScreenEvent {
    data object OnBackClick : ViewRagDocumentScreenEvent
    data class OnQueryChange(val value: String) : ViewRagDocumentScreenEvent
    data object OnSearchClick : ViewRagDocumentScreenEvent
}
