package com.github.mobdev778.aiadventchallenge.data.settings.datasource.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class SettingsEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int = SINGLETON_ID,

    @ColumnInfo(name = "message_selection_type")
    val messageSelectionType: String,

    @ColumnInfo(name = "max_messages")
    val maxMessages: Int,

    @ColumnInfo(name = "max_tokens")
    val maxTokens: Int,

    @ColumnInfo(name = "recursive_summation_max_messages")
    val recursiveSummationMaxMessages: Int,

    @ColumnInfo(name = "api_key")
    val apiKey: String,

    @ColumnInfo(name = "base_url")
    val baseUrl: String,

    @ColumnInfo(name = "base_model")
    val baseModel: String,
) {
    companion object {
        const val SINGLETON_ID: Int = 1
    }
}
