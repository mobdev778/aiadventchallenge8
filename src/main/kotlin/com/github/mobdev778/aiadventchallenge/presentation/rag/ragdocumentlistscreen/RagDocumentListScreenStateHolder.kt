package com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen

import com.github.mobdev778.aiadventchallenge.data.rag.repository.RagDocumentRepository
import com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.model.RagDocumentListItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import java.util.UUID

@Single
class RagDocumentListScreenStateHolder(
    private val ragDocumentRepository: RagDocumentRepository,
    private val scope: CoroutineScope,
) {

    val documents: StateFlow<List<RagDocumentListItem>> = ragDocumentRepository
        .observeDocuments()
        .map { documents ->
            documents.map { document ->
                RagDocumentListItem(
                    id = document.id,
                    source = document.source,
                    title = document.title,
                    chunkCount = ragDocumentRepository.getChunkCount(document.id),
                )
            }
        }
        .flowOn(Dispatchers.IO)
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())

    val commands = MutableSharedFlow<RagDocumentListScreenCommand>(
        extraBufferCapacity = 1,
    )

    fun onEvent(event: RagDocumentListScreenEvent) {
        when (event) {
            RagDocumentListScreenEvent.OnBackClick -> commands.tryEmit(RagDocumentListScreenCommand.Back)
            RagDocumentListScreenEvent.OnAddClick -> commands.tryEmit(RagDocumentListScreenCommand.OpenAddDocument)
            is RagDocumentListScreenEvent.OnDocumentClick -> {
                commands.tryEmit(RagDocumentListScreenCommand.OpenDocument(event.document))
            }
            is RagDocumentListScreenEvent.OnDeleteClick -> deleteDocument(event.documentId)
        }
    }

    private fun deleteDocument(documentId: UUID) {
        scope.launch(Dispatchers.IO) {
            ragDocumentRepository.deleteDocument(documentId)
        }
    }
}
