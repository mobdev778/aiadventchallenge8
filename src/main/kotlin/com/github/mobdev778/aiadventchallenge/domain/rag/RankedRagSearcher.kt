package com.github.mobdev778.aiadventchallenge.domain.rag

import com.github.mobdev778.aiadventchallenge.data.rag.repository.RagConfigRepository
import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagFilterType
import com.github.mobdev778.aiadventchallenge.domain.rag.queryrewriter.QueryRewriter
import com.github.mobdev778.aiadventchallenge.domain.rag.ranker.RankerFactory
import org.koin.core.annotation.Single

@Single
class RankedRagSearcher(
    private val searcher: SimpleRagSearcher,
    private val configRepository: RagConfigRepository,
    private val queryRewriter: QueryRewriter,
    private val rankerFactory: RankerFactory,
) : RagSearcher {

    override suspend fun search(query: String): List<RagSearchResult> {
        val config = configRepository.getConfig()

        val result = ArrayList<RagSearchResult>()

        // 1) сначала получаем обычный результат
        if (config.useQueryRewriting) {
            val queries = queryRewriter.getQueries(query, 3)
            queries.forEach {
                result.addAll(searcher.search(it))
            }
        } else {
            result.addAll(searcher.search(query))
        }

        // 2) затем используем ranker для ресортировки результатов
        val filterType = config.filterType
        val ranker = rankerFactory.create(filterType)

        ranker.init(query)

        val rankedResult = result.map {
            val rank = ranker.rank(it.text, it.vector)
            rank to it
        }
        val sorted = rankedResult.sortedByDescending { it.first }.map { it.second }

        // 3) обираем "topKAfter" записей
        val filtered = sorted.take(config.topKAfter)

        // 4) отбираем через жесткое сравнение, если нужно
        return when {
            config.useMinSimilarity -> {
                val similarityRanker = rankerFactory.create(RagFilterType.Similarity)
                similarityRanker.init(query)
                filtered.filter {
                    val rank = similarityRanker.rank(it.text, it.vector)
                    rank >= config.minSimilarity
                }
            }
            else -> filtered
        }
    }
}