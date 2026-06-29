package com.github.mobdev778.aiadventchallenge.presentation.rag.addingragdocumentscreen

sealed interface AddingRagDocumentScreenEvent {
    data object OnAbortClick : AddingRagDocumentScreenEvent
}
