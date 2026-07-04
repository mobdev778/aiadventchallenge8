package com.github.mobdev778.aiadventchallenge.domain.rag.ranker

import com.github.mobdev778.aiadventchallenge.domain.rag.RagChunkGenerator

class SimilarityRanker(
    val ragChunkGenerator: RagChunkGenerator,
) : Ranker {

    private var queryVector: FloatArray? = null

    override suspend fun init(query: String) {
        queryVector = ragChunkGenerator
            .generate(section = 0, text = query)
            .vector
    }

    override suspend fun rank(found: String, vector: FloatArray): Double {
        val qVector = queryVector ?: return Double.NEGATIVE_INFINITY
        return qVector.cosineSimilarity(vector)
    }
}
