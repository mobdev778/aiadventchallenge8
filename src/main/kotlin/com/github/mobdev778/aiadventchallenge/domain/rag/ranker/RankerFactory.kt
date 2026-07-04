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

@Single
class RankerFactory(
    private val ragConfigRepository: RagConfigRepository,
) {

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
