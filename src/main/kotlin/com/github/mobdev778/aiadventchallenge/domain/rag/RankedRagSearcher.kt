package com.github.mobdev778.aiadventchallenge.domain.rag

import com.github.mobdev778.aiadventchallenge.data.rag.repository.RagConfigRepository
import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagFilterType
import com.github.mobdev778.aiadventchallenge.domain.rag.queryrewriter.QueryRewriter
import com.github.mobdev778.aiadventchallenge.domain.rag.ranker.RankerFactory
import org.koin.core.annotation.Single
import java.util.UUID

/**
 * Реализация [RagSearcher], выполняющая многоэтапный поиск релевантных фрагментов
 * с возможностью переформулировки запроса, ранжирования и фильтрации по минимальному сходству.
 *
 * Данный компонент координирует работу нескольких подсистем:
 * - [SimpleRagSearcher] для первичного семантического поиска
 * - [QueryRewriter] для генерации альтернативных формулировок исходного запроса
 * - [RankerFactory] для создания ранкера в соответствии с выбранной стратегией ([RagFilterType])
 * - [RagConfigRepository] для получения актуальных настроек RAG
 *
 * Алгоритм поиска включает следующие шаги:
 * 1. Получение конфигурации RAG из [configRepository].
 * 2. Первичный поиск: если разрешено переформулирование запроса ([RagConfig.useQueryRewriting]),
 *    генерируются дополнительные формулировки и для каждой выполняется поиск с лимитом [RagConfig.topKBefore];
 *    иначе поиск сразу ограничивается [RagConfig.topKAfter].
 * 3. Ранжирование всех найденных результатов с помощью ранкера, соответствующего [RagConfig.filterType].
 * 4. Извлечение первых [RagConfig.topKAfter] элементов по убыванию скоров.
 * 5. При необходимости ([RagConfig.useMinSimilarity]) дополнительная фильтрация по порогу
 *    [RagConfig.minSimilarity] с использованием Similarity-ранкера.
 * 6. Ограничение итогового списка до [maxResults].
 *
 * Позволяет гибко настраивать баланс между полнотой и точностью поиска за счёт
 * комбинирования различных методов отбора и переранжирования.
 *
 * @param searcher базовый поисковый движок для извлечения кандидатов по эмбеддингам
 * @param configRepository источник конфигурации RAG
 * @param queryRewriter компонент для расширения запроса (генерации вариантов)
 * @param rankerFactory фабрика, создающая стратегию ранжирования по типу [RagFilterType]
 */
@Single
class RankedRagSearcher(
    private val searcher: SimpleRagSearcher,
    private val configRepository: RagConfigRepository,
    private val queryRewriter: QueryRewriter,
    private val rankerFactory: RankerFactory,
) : RagSearcher {

    /**
     * Выполняет ранжированный поиск релевантных фрагментов документа.
     *
     * @param documentId уникальный идентификатор документа
     * @param query исходный поисковый запрос пользователя
     * @param maxResults максимальное количество возвращаемых результатов
     * @return список объектов [RagSearchResult], отсортированных по убыванию релевантности
     */
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
        /**
         * Количество переформулированных запросов, генерируемых [QueryRewriter],
         * когда включено расширение запроса ([RagConfig.useQueryRewriting]).
         */
        private const val QUERY_REWRITE_COUNT = 3
    }
}
