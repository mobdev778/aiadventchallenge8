package com.github.mobdev778.aiadventchallenge.presentation.rag.addingragdocumentscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.github.mobdev778.aiadventchallenge.presentation.rag.addingragdocumentscreen.composable.AddingRagDocumentScreenContent
import com.github.mobdev778.aiadventchallenge.presentation.rag.addragdocumentscreen.model.AddRagDocumentScreenState
import org.koin.java.KoinJavaComponent.inject

@Composable
fun AddingRagDocumentScreen(
    source: String,
    title: String,
    chunkingStrategy: AddRagDocumentScreenState.ChunkingStrategy,
    onBackToAddDocument: () -> Unit,
    onBackToDocumentList: () -> Unit,
) {
    val stateHolder = remember {
        inject<AddingRagDocumentScreenStateHolder>(AddingRagDocumentScreenStateHolder::class.java).value
    }

    val state by stateHolder.state.collectAsState()

    LaunchedEffect(source, title, chunkingStrategy) {
        stateHolder.start(
            source = source,
            title = title,
            chunkingStrategy = chunkingStrategy,
        )
    }

    AddingRagDocumentScreenContent(
        state = state,
        onEvent = stateHolder::onEvent,
    )

    LaunchedEffect(Unit) {
        stateHolder.commands.collect { command ->
            when (command) {
                AddingRagDocumentScreenCommand.BackToAddDocument -> onBackToAddDocument()
                AddingRagDocumentScreenCommand.BackToDocumentList -> onBackToDocumentList()
            }
        }
    }
}
