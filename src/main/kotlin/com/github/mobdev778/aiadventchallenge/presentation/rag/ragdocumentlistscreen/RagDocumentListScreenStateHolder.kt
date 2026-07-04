package com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen

import com.github.mobdev778.aiadventchallenge.data.rag.repository.RagConfigRepository
import com.github.mobdev778.aiadventchallenge.data.rag.repository.RagDocumentRepository
import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagConfig
import com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.model.RagDocumentListItem
import com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.model.RagDocumentListScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import java.util.UUID

private const val STATE_FLOW_TIMEOUT_MS = 5000L

@Single
class RagDocumentListScreenStateHolder(
    private val ragDocumentRepository: RagDocumentRepository,
    private val ragConfigRepository: RagConfigRepository,
    private val scope: CoroutineScope,
) {

    private val documentsFlow = ragDocumentRepository
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

    val uiState: StateFlow<RagDocumentListScreenState> = combine(
        ragConfigRepository.observeConfig(),
        documentsFlow,
    ) { ragConfig: RagConfig, documents: List<RagDocumentListItem> ->
        RagDocumentListScreenState(
            ragConfig = ragConfig,
            documents = documents,
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(STATE_FLOW_TIMEOUT_MS),
        initialValue = RagDocumentListScreenState(
            ragConfig = RagConfigRepository.default,
            documents = emptyList(),
        ),
    )

    val commands = MutableSharedFlow<RagDocumentListScreenCommand>(
        extraBufferCapacity = 1,
    )

    fun onEvent(event: RagDocumentListScreenEvent) {
        when (event) {
            RagDocumentListScreenEvent.OnBackClick -> commands.tryEmit(RagDocumentListScreenCommand.Back)
            RagDocumentListScreenEvent.OnAddClick -> commands.tryEmit(RagDocumentListScreenCommand.OpenAddDocument)
            RagDocumentListScreenEvent.OnRagConfigClick -> commands.tryEmit(RagDocumentListScreenCommand.OpenRagConfig)
            is RagDocumentListScreenEvent.OnDocumentClick -> {
                commands.tryEmit(RagDocumentListScreenCommand.OpenDocument(event.document))
            }
            is RagDocumentListScreenEvent.OnDeleteClick -> deleteDocument(event.documentId)
        }
    }

    private fun updateConfig(transform: RagConfig.() -> RagConfig) {
        scope.launch(Dispatchers.IO) {
            val updatedConfig = ragConfigRepository.getConfig().transform()
            ragConfigRepository.updateConfig(updatedConfig)
        }
    }

    private fun deleteDocument(documentId: UUID) {
        scope.launch(Dispatchers.IO) {
            ragDocumentRepository.deleteDocument(documentId)
        }
    }
}
