package com.github.mobdev778.aiadventchallenge.domain.rag.ranker

import com.github.mobdev778.aiadventchallenge.data.rag.repository.RagConfigRepository
import com.github.mobdev778.aiadventchallenge.domain.rag.RagChunkGenerator
import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagFilterType
import com.jetbrains.rd.util.UUID
import dev.langchain4j.model.embedding.onnx.OnnxEmbeddingModel
import dev.langchain4j.model.embedding.onnx.PoolingMode
import dev.langchain4j.model.scoring.onnx.OnnxScoringModel
import kotlinx.coroutines.runBlocking
import org.koin.core.annotation.Single
import java.io.File

/**
 * Фабрика для создания конкретных реализаций [Ranker] на основе [RagFilterType].
 *
 * Отвечает за ленивую инициализацию моделей эмбеддингов ([embeddingModel]) и скоринга
 * ([scoringModel]), которые используются в [SimilarityRanker] и [ReRanker] соответственно.
 * Пути к файлам моделей извлекаются из конфигурации RAG через [RagConfigRepository].
 *
 * Аннотирована как [Single] для регистрации в Koin-контейнере в качестве singleton-компонента.
 *
 * @property ragConfigRepository репозиторий конфигурации RAG, предоставляющий пути к ONNX-моделям
 */
@Single
class RankerFactory(
    private val ragConfigRepository: RagConfigRepository,
) {

    /**
     * Лениво инициализируемая ONNX-модель эмбеддингов.
     *
     * Загружается при первом обращении с использованием путей из текущей
     * конфигурации RAG. Для корректной загрузки в окружении с изолированными
     * загрузчиками классов временно подменяется контекстный ClassLoader потока.
     */
    val embeddingModel: OnnxEmbeddingModel by lazy {
        val ragConfig = runBlocking {
            ragConfigRepository.getConfig()
        }
        // Save the original ClassLoader to restore it later
        val originalClassLoader = Thread.currentThread().contextClassLoader
        try {
            // Force the thread to use the ClassLoader that loaded OnnxEmbeddingModel
            Thread.currentThread().contextClassLoader = OnnxEmbeddingModel::class.java.classLoader

            OnnxEmbeddingModel(
                File(ragConfig.embModelPath).path,
                File(ragConfig.embTokenizerPath).path,
                PoolingMode.MEAN
            )
        } finally {
            // Always restore the original ClassLoader
            Thread.currentThread().contextClassLoader = originalClassLoader
        }
    }

    /**
     * Лениво инициализируемая ONNX-модель скоринга (реранкер).
     *
     * Аналогично [embeddingModel], загружается при первом обращении с подменой
     * контекстного ClassLoader для совместимости с окружением загрузки ONNX-библиотек.
     */
    val scoringModel: OnnxScoringModel by lazy {
        val ragConfig = runBlocking {
            ragConfigRepository.getConfig()
        }
        val originalClassLoader = Thread.currentThread().contextClassLoader
        try {
            // Force the thread to use the ClassLoader that loaded OnnxEmbeddingModel
            Thread.currentThread().contextClassLoader = OnnxEmbeddingModel::class.java.classLoader

            OnnxScoringModel(
                ragConfig.rankerModelPath,
                ragConfig.rankerTokenizerPath,
            )
        } finally {
            // Always restore the original ClassLoader
            Thread.currentThread().contextClassLoader = originalClassLoader
        }
    }

    /**
     * Создаёт экземпляр [Ranker], соответствующий переданному типу фильтрации.
     *
     * @param filterType тип фильтрации, определяющий используемый алгоритм ранжирования
     * @return готовый к использованию ранкер:
     *         - [SimilarityRanker] для [RagFilterType.Similarity],
     *         - [ReRanker] для [RagFilterType.Reranker],
     *         - [HeuristicRanker] для [RagFilterType.Heuristic]
     */
    fun create(filterType: RagFilterType): Ranker {
        return when (filterType) {
            RagFilterType.Reranker -> ReRanker(scoringModel)
            RagFilterType.Heuristic -> HeuristicRanker()
            RagFilterType.Similarity -> SimilarityRanker(
                RagChunkGenerator(UUID.randomUUID(), embeddingModel)
            )
        }
    }
}
