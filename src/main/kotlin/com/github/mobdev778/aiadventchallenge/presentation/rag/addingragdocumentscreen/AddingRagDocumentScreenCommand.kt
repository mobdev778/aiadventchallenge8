package com.github.mobdev778.aiadventchallenge.presentation.rag.addingragdocumentscreen

sealed interface AddingRagDocumentScreenCommand {
    data object BackToAddDocument : AddingRagDocumentScreenCommand
    data object BackToDocumentList : AddingRagDocumentScreenCommand
}
