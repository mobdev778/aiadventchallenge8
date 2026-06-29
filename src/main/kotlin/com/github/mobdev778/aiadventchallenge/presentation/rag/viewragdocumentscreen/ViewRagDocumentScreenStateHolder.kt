package com.github.mobdev778.aiadventchallenge.presentation.rag.viewragdocumentscreen

import com.github.mobdev778.aiadventchallenge.data.rag.repository.RagDocumentRepository
import com.github.mobdev778.aiadventchallenge.domain.rag.RagChunkGenerator
import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagDocumentChunk
import com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.model.RagDocumentListItem
import com.github.mobdev778.aiadventchallenge.presentation.rag.viewragdocumentscreen.model.ViewRagDocumentScreenState
import com.github.mobdev778.aiadventchallenge.presentation.rag.viewragdocumentscreen.model.ViewRagDocumentSearchResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory
import java.util.PriorityQueue
import java.util.UUID
import kotlin.math.sqrt

@Factory
class ViewRagDocumentScreenStateHolder(
    private val ragDocumentRepository: RagDocumentRepository,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(ViewRagDocumentScreenState())
    val state: StateFlow<ViewRagDocumentScreenState> = _state.asStateFlow()

    val commands = MutableSharedFlow<ViewRagDocumentScreenCommand>(
        extraBufferCapacity = 1,
    )

    fun start(document: RagDocumentListItem) {
        _state.update { current ->
            if (current.document?.id == document.id) current else current.copy(document = document)
        }
    }

    fun onEvent(event: ViewRagDocumentScreenEvent) {
        when (event) {
            ViewRagDocumentScreenEvent.OnBackClick -> commands.tryEmit(ViewRagDocumentScreenCommand.Back)
            is ViewRagDocumentScreenEvent.OnQueryChange -> {
                _state.update { it.copy(query = event.value) }
            }
            ViewRagDocumentScreenEvent.OnSearchClick -> search()
        }
    }

    private fun search() {
        val snapshot = state.value
        val document = snapshot.document ?: return
        val query = snapshot.query.trim()
        if (query.isEmpty()) return

        scope.launch(Dispatchers.IO) {
            _state.update { it.copy(isSearching = true, results = emptyList()) }

            val queryChunk = RagChunkGenerator(document.id).generate(
                section = 0,
                text = query,
            )
            val queryVector = queryChunk.vector

            val bestChunks = PriorityQueue<Pair<Double, RagDocumentChunk>>(
                compareBy { it.first }
            )
            val pageSize = 10
            var offset = 0

            while (true) {
                val page = ragDocumentRepository.getChunksPage(
                    documentId = document.id,
                    limit = pageSize,
                    offset = offset,
                )
                if (page.isEmpty()) break

                page.forEach { chunk ->
                    val similarity = cosineSimilarity(queryVector, chunk.vector)
                    bestChunks.offer(similarity to chunk)
                    if (bestChunks.size > 3) {
                        bestChunks.poll()
                    }
                }

                offset += pageSize
            }

            val results = bestChunks
                .sortedByDescending { it.first }
                .map { (_, chunk) ->
                ViewRagDocumentSearchResult(
                    source = document.source,
                    section = chunk.section,
                    text = chunk.text,
                )
            }

            _state.update { it.copy(isSearching = false, results = results) }
        }
    }

    private fun cosineSimilarity(left: FloatArray, right: FloatArray): Double {
        if (left.isEmpty() || right.isEmpty() || left.size != right.size) return Double.NEGATIVE_INFINITY

        var dot = 0.0
        var leftNorm = 0.0
        var rightNorm = 0.0

        for (index in left.indices) {
            val l = left[index].toDouble()
            val r = right[index].toDouble()
            dot += l * r
            leftNorm += l * l
            rightNorm += r * r
        }

        if (leftNorm == 0.0 || rightNorm == 0.0) return Double.NEGATIVE_INFINITY

        return dot / (sqrt(leftNorm) * sqrt(rightNorm))
    }
}
