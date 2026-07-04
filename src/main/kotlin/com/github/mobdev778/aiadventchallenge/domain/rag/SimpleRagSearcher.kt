package com.github.mobdev778.aiadventchallenge.domain.rag

import com.github.mobdev778.aiadventchallenge.data.rag.repository.RagConfigRepository
import com.github.mobdev778.aiadventchallenge.data.rag.repository.RagDocumentRepository
import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagDocumentChunk
import com.github.mobdev778.aiadventchallenge.domain.rag.ranker.RankerFactory
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Single
import java.util.PriorityQueue
import kotlin.math.sqrt

@Single
class SimpleRagSearcher(
    private val documentRepository: RagDocumentRepository,
    private val configRepository: RagConfigRepository,
    private val rankerFactory: RankerFactory,
) : RagSearcher {

    override suspend fun search(query: String): List<RagSearchResult> {
        val config = configRepository.getConfig()

        val document = documentRepository.observeDocuments().first().firstOrNull() ?: return emptyList()

        val queryVector = RagChunkGenerator(document.id, rankerFactory.embeddingModel)
            .generate(section = 0, text = query)
            .vector

        val bestChunks = PriorityQueue<Pair<Double, RagDocumentChunk>>(
            compareBy { it.first }
        )
        val pageSize = CHUNK_PAGE_SIZE
        var offset = 0

        while (true) {
            val page = documentRepository.getChunksPage(
                documentId = document.id,
                limit = pageSize,
                offset = offset,
            )
            if (page.isEmpty()) break

            page.forEach { chunk ->
                val score = cosineSimilarity(queryVector, chunk.vector)
                bestChunks.offer(score to chunk)
                if (bestChunks.size > config.topKBefore) {
                    bestChunks.poll()
                }
            }

            offset += pageSize
        }

        val results = bestChunks
            .sortedByDescending { it.first }
            .map { (score, chunk) ->
                RagSearchResult(
                    source = document.source,
                    section = chunk.section,
                    text = chunk.text,
                    vector = chunk.vector,
                    score = score,
                )
            }

        println("!!! RAG searcher. results: ${results}")

        return results
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

        val result = if (leftNorm == 0.0 || rightNorm == 0.0) {
            Double.NEGATIVE_INFINITY
        } else {
            dot / (sqrt(leftNorm) * sqrt(rightNorm))
        }
        return result
    }

    companion object {
        private const val CHUNK_PAGE_SIZE = 10
    }
}
