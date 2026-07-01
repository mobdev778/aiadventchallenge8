package com.github.mobdev778.aiadventchallenge.data.rag.datasource.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rag_config")
data class RagConfigEntity(
    @PrimaryKey
    val id: Int = SINGLETON_ID,
    val topKBefore: Int,
    val filterType: String,
    val useQueryRewriting: Boolean,
    val topKAfter: Int,
    val useMinSimilarity: Boolean,
    val minSimilarity: Double,
    val rankerModelPath: String,
    val rankerTokenizerPath: String,
    val embModelPath: String,
    val embTokenizerPath: String,
) {
    companion object {
        const val SINGLETON_ID: Int = 1
    }
}
