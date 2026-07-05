package com.github.mobdev778.aiadventchallenge.domain.rag

import com.github.mobdev778.aiadventchallenge.data.rag.repository.RagDocumentRepository
import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagDocumentChunk
import com.github.mobdev778.aiadventchallenge.domain.rag.ranker.RankerFactory
import com.github.mobdev778.aiadventchallenge.domain.rag.ranker.cosineSimilarity
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.annotation.Single
import java.util.PriorityQueue
import java.util.UUID

@Single
class SimpleRagSearcher(
    private val documentRepository: RagDocumentRepository,
    private val rankerFactory: RankerFactory,
) : RagSearcher {

    override suspend fun search(
        documentId: UUID,
        query: String,
        maxResults: Int
    ): List<RagSearchResult> {
        val document = documentRepository.observeDocuments().firstOrNull()
            ?.firstOrNull { it.id == documentId } ?: return emptyList()

        val queryVector = RagChunkGenerator(documentId, rankerFactory.embeddingModel)
            .generate(section = 0, text = query)
            .vector

        val bestChunks = PriorityQueue<Pair<Double, RagDocumentChunk>>(
            compareBy { it.first }
        )
        val pageSize = CHUNK_PAGE_SIZE
        var offset = 0

        while (true) {
            val page = documentRepository.getChunksPage(
                documentId = documentId,
                limit = pageSize,
                offset = offset,
            )
            if (page.isEmpty()) break

            page.forEach { chunk ->
                val score = queryVector.cosineSimilarity(chunk.vector)
                bestChunks.offer(score to chunk)
                if (bestChunks.size > maxResults) {
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

    companion object {
        private const val CHUNK_PAGE_SIZE = 10
    }
}
