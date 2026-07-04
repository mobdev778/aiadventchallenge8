package com.github.mobdev778.aiadventchallenge.domain.rag.ranker

import dev.langchain4j.model.scoring.onnx.OnnxScoringModel

class ReRanker(
    private val scoringModel: OnnxScoringModel,
) : Ranker {

    private var query: String = ""

    override suspend fun init(query: String) {
        this.query = query
    }

    override suspend fun rank(found: String, vector: FloatArray): Double {
        return scoringModel.score(query, found).content()
    }
}
