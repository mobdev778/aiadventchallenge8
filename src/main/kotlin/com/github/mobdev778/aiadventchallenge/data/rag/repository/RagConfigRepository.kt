package com.github.mobdev778.aiadventchallenge.data.rag.repository

import com.github.mobdev778.aiadventchallenge.data.rag.datasource.RagDocumentDao
import com.github.mobdev778.aiadventchallenge.data.rag.datasource.model.RagConfigEntity
import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagConfig
import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagFilterType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
class RagConfigRepository(
    private val ragDocumentDao: RagDocumentDao,
) {

    fun observeConfig(): Flow<RagConfig> =
        ragDocumentDao.observeConfig()
            .map { entity -> entity?.toDomain() ?: default }
            .distinctUntilChanged()

    suspend fun getConfig(): RagConfig {
        return ragDocumentDao.getConfig()?.toDomain() ?: default
    }

    suspend fun updateConfig(config: RagConfig) {
        ragDocumentDao.upsertConfig(config.toEntity())
    }

    private fun RagConfigEntity.toDomain(): RagConfig {
        val filterType = runCatching { RagFilterType.valueOf(filterType) }
            .getOrDefault(RagFilterType.Similarity)

        return RagConfig(
            topKBefore = topKBefore,
            filterType = filterType,
            useQueryRewriting = useQueryRewriting,
            topKAfter = topKAfter,
            useMinSimilarity = useMinSimilarity,
            minSimilarity = minSimilarity,
            rankerModelPath = rankerModelPath,
            rankerTokenizerPath = rankerTokenizerPath,
            embModelPath = embModelPath,
            embTokenizerPath = embTokenizerPath,
        )
    }

    private fun RagConfig.toEntity(): RagConfigEntity =
        RagConfigEntity(
            id = RagConfigEntity.SINGLETON_ID,
            topKBefore = topKBefore,
            filterType = filterType.name,
            useQueryRewriting = useQueryRewriting,
            topKAfter = topKAfter,
            useMinSimilarity = useMinSimilarity,
            minSimilarity = minSimilarity,
            rankerModelPath = rankerModelPath,
            rankerTokenizerPath = rankerTokenizerPath,
            embModelPath = embModelPath,
            embTokenizerPath = embTokenizerPath,
        )

    companion object {
        val default = RagConfig(
            topKBefore = 10,
            filterType = RagFilterType.Similarity,
            useQueryRewriting = false,
            topKAfter = 5,
            useMinSimilarity = false,
            minSimilarity = 0.5,
            rankerModelPath = "",
            rankerTokenizerPath = "",
            embModelPath = "",
            embTokenizerPath = "",
        )
    }
}
