package com.github.mobdev778.aiadventchallenge.domain.rag

import com.github.mobdev778.aiadventchallenge.data.rag.repository.RagDocumentRepository
import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagDocumentChunk
import com.github.mobdev778.aiadventchallenge.domain.rag.ranker.RankerFactory
import com.github.mobdev778.aiadventchallenge.domain.rag.ranker.cosineSimilarity
import kotlinx.coroutines.flow.firstOrNull
import org.koin.core.annotation.Single
import java.util.PriorityQueue
import java.util.UUID

/**
 * Простая реализация интерфейса [RagSearcher], выполняющая поиск релевантных фрагментов документа
 * на основе косинусного сходства между вектором запроса и векторными представлениями чанков.
 *
 * Использует [RagDocumentRepository] для доступа к документу и его чанкам, а [RankerFactory] —
 * для получения эмбеддинговой модели, необходимой для векторизации пользовательского запроса.
 * Поиск осуществляется постранично с фиксацией лучших результатов через очередь с приоритетом,
 * что позволяет эффективно находить top-N наиболее похожих чанков даже для больших документов.
 *
 * @property documentRepository репозиторий для работы с документами и чанками.
 * @property rankerFactory фабрика ранкеров, предоставляющая эмбеддинговую модель.
 */
@Single
class SimpleRagSearcher(
    private val documentRepository: RagDocumentRepository,
    private val rankerFactory: RankerFactory,
) : RagSearcher {

    /**
     * Выполняет поиск наиболее релевантных фрагментов документа по текстовому запросу.
     *
     * Алгоритм:
     * 1. Получает документ по [documentId] из наблюдаемого списка документов.
     * 2. Генерирует вектор запроса с помощью эмбеддинговой модели через [RagChunkGenerator].
     * 3. Постранично загружает чанки документа и вычисляет косинусное сходство
 *    между вектором запроса и вектором каждого чанка.
     * 4. Поддерживает очередь лучших [maxResults] результатов на основе минимального сходства.
     * 5. Возвращает отсортированный по убыванию релевантности список [RagSearchResult].
     *
     * @param documentId идентификатор документа, в котором производится поиск.
     * @param query текстовый запрос пользователя.
     * @param maxResults максимальное количество возвращаемых результатов.
     * @return список объектов [RagSearchResult], содержащих информацию о наиболее релевантных фрагментах.
     *         Если документ не найден, возвращается пустой список.
     */
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
