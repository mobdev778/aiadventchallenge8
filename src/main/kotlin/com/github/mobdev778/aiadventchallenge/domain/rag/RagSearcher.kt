package com.github.mobdev778.aiadventchallenge.domain.rag

import com.github.mobdev778.aiadventchallenge.data.rag.repository.RagDocumentRepository
import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagDocumentChunk
import com.github.mobdev778.aiadventchallenge.presentation.rag.viewragdocumentscreen.model.ViewRagDocumentSearchResult
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Single
import java.util.PriorityQueue
import kotlin.math.sqrt

@Single
class RagSearcher(
    private val ragDocumentRepository: RagDocumentRepository,
) {

    suspend fun search(query: String): List<String> {
        val document = ragDocumentRepository.observeDocuments().first().firstOrNull() ?: return emptyList()

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

        println("!!! RAG searcher. results: ${results}")

        return results.map { it.text }
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