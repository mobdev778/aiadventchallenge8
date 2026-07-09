package com.github.mobdev778.aiadventchallenge.domain.rag

import com.github.mobdev778.aiadventchallenge.data.rag.repository.RagConfigRepository
import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagFilterType
import com.github.mobdev778.aiadventchallenge.domain.rag.queryrewriter.QueryRewriter
import com.github.mobdev778.aiadventchallenge.domain.rag.ranker.RankerFactory
import org.koin.core.annotation.Single
import java.util.UUID

@Single
class RankedRagSearcher(
    private val searcher: SimpleRagSearcher,
    private val configRepository: RagConfigRepository,
    private val queryRewriter: QueryRewriter,
    private val rankerFactory: RankerFactory,
) : RagSearcher {

    override suspend fun search(documentId: UUID, query: String, maxResults: Int): List<RagSearchResult> {
        val config = configRepository.getConfig()

        val result = ArrayList<RagSearchResult>()

        // 1) сначала получаем обычный результат
        if (config.useQueryRewriting) {
            val queries = queryRewriter.getQueries(query, QUERY_REWRITE_COUNT)
            queries.forEach {
                result.addAll(searcher.search(documentId, it, config.topKBefore))
            }
        } else {
            result.addAll(searcher.search(documentId, query, config.topKAfter))
        }

        // 2) затем используем ranker для ресортировки результатов
        val filterType = config.filterType
        val ranker = rankerFactory.create(filterType)

        ranker.init(query)

        val rankedResult = result.map {
            val score = ranker.rank(it.text, it.vector)
            it.copy(score = score)
        }
        val sorted = rankedResult.sortedByDescending { it.score }

        // 3) обираем "topKAfter" записей
        val filtered = sorted.take(config.topKAfter)

        // 4) отбираем через жесткое сравнение, если нужно
        return when {
            config.useMinSimilarity -> {
                val similarityRanker = rankerFactory.create(RagFilterType.Similarity)
                similarityRanker.init(query)
                filtered.filter {
                    val score = similarityRanker.rank(it.text, it.vector)
                    score >= config.minSimilarity
                }.take(maxResults)
            }
            else -> filtered.take(maxResults)
        }
    }

    companion object {
        private const val QUERY_REWRITE_COUNT = 3
    }
}
