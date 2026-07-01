package com.github.mobdev778.aiadventchallenge.presentation.rag.addingragdocumentscreen

import com.github.mobdev778.aiadventchallenge.data.rag.repository.RagConfigRepository
import com.github.mobdev778.aiadventchallenge.data.rag.repository.RagDocumentRepository
import com.github.mobdev778.aiadventchallenge.domain.rag.RagChunkGenerator
import com.github.mobdev778.aiadventchallenge.domain.rag.filechunker.FileChunkerFactory
import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagDocument
import com.github.mobdev778.aiadventchallenge.domain.rag.ranker.RankerFactory
import com.github.mobdev778.aiadventchallenge.presentation.rag.addingragdocumentscreen.model.AddingRagDocumentScreenState
import com.github.mobdev778.aiadventchallenge.presentation.rag.addragdocumentscreen.model.AddRagDocumentScreenState.ChunkingStrategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import java.io.File
import java.util.UUID

@Factory
class AddingRagDocumentScreenStateHolder(
    private val ragDocumentRepository: RagDocumentRepository,
    private val ragConfigRepository: RagConfigRepository,
    private val rankerFactory: RankerFactory,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(AddingRagDocumentScreenState())
    val state: StateFlow<AddingRagDocumentScreenState> = _state.asStateFlow()

    val commands = MutableSharedFlow<AddingRagDocumentScreenCommand>(
        extraBufferCapacity = 1,
    )

    private var progressJob: Job? = null

    fun start(
        source: String,
        title: String,
        chunkingStrategy: ChunkingStrategy,
    ) {
        if (progressJob != null) return

        progressJob = scope.launch(Dispatchers.IO) {
            val document = RagDocument(
                id = UUID.randomUUID(),
                source = source,
                title = title,
            )
            ragDocumentRepository.createDocument(document)

            val total = getTotalChunks(source, chunkingStrategy)

            val ragChunkGenerator = RagChunkGenerator(
                document.id,
                rankerFactory.embeddingModel
            )

            val fileChunker = FileChunkerFactory().create(File(source), chunkingStrategy)
            while (true) {
                val fileChunk = fileChunker.next() ?: break
                val ragChunk = ragChunkGenerator.generate(fileChunk.section, fileChunk.text)
                ragDocumentRepository.add(ragChunk)
                _state.update { it.copy(progress = fileChunk.section / total.toFloat()) }
            }

            commands.tryEmit(AddingRagDocumentScreenCommand.BackToDocumentList)
        }
    }

    fun onEvent(event: AddingRagDocumentScreenEvent) {
        when (event) {
            AddingRagDocumentScreenEvent.OnAbortClick -> abort()
        }
    }

    private fun abort() {
        progressJob?.cancel()
        progressJob = null
        _state.update { it.copy(progress = 0f) }
        commands.tryEmit(AddingRagDocumentScreenCommand.BackToAddDocument)
    }

    private fun getTotalChunks(source: String, chunkingStrategy: ChunkingStrategy): Int {
        val fileChunker = FileChunkerFactory().create(File(source), chunkingStrategy)
        var chunks = 0
        while (true) {
            val chunk = fileChunker.next() ?: break
            chunks++
        }
        return chunks
    }
}
