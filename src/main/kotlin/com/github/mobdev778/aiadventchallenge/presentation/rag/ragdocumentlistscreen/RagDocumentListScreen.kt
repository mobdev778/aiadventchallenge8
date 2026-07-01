package com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.composable.RagDocumentListScreenContent
import com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.model.RagDocumentListItem
import org.koin.java.KoinJavaComponent.inject

@Composable
fun RagDocumentListScreen(
    onBack: () -> Unit,
    onOpenAddDocument: () -> Unit,
    onOpenDocument: (RagDocumentListItem) -> Unit,
    onOpenRagConfig: () -> Unit,
) {
    val stateHolder = remember {
        inject<RagDocumentListScreenStateHolder>(RagDocumentListScreenStateHolder::class.java).value
    }

    val uiState by stateHolder.uiState.collectAsState()

    RagDocumentListScreenContent(
        documents = uiState.documents,
        onEvent = stateHolder::onEvent,
    )

    LaunchedEffect(Unit) {
        stateHolder.commands.collect { command ->
            when (command) {
                RagDocumentListScreenCommand.Back -> onBack()
                RagDocumentListScreenCommand.OpenAddDocument -> onOpenAddDocument()
                RagDocumentListScreenCommand.OpenRagConfig -> onOpenRagConfig()
                is RagDocumentListScreenCommand.OpenDocument -> onOpenDocument(command.document)
            }
        }
    }
}
