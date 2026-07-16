package com.github.mobdev778.aiadventchallenge.data.settings.datasource.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity для хранения настроек приложения в таблице "app_settings".
 *
 * Использует паттерн singleton-записи: в таблице всегда существует ровно одна строка
 * с фиксированным первичным ключом, равным [SINGLETON_ID]. Все настройки хранятся в полях
 * этой единственной записи.
 *
 * @property id Уникальный идентификатор singleton-записи (всегда равен [SINGLETON_ID]).
 * @property contextManagementType Тип управления контекстом (например, "recursive_summation" или "sticky_facts").
 * @property maxMessages Максимальное количество сообщений, сохраняемых в контексте беседы.
 * @property maxTokens Максимальное количество токенов, выделяемое на историю диалога.
 * @property recursiveSummationMaxMessages Максимальное количество сообщений, используемых при рекурсивном суммировании.
 * @property stickyFactsMaxMessages Максимальное количество сообщений, из которых извлекаются «прикреплённые факты».
 * @property apiKey API-ключ для доступа к LLM.
 * @property baseUrl Базовый URL сервера, обслуживающего модель.
 * @property baseModel Идентификатор используемой по умолчанию модели (например, "gpt-4").
 */
@Entity(tableName = "app_settings")
data class SettingsEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int = SINGLETON_ID,

    @ColumnInfo(name = "context_management_type")
    val contextManagementType: String,

    @ColumnInfo(name = "max_messages")
    val maxMessages: Int,

    @ColumnInfo(name = "max_tokens")
    val maxTokens: Int,

    @ColumnInfo(name = "recursive_summation_max_messages")
    val recursiveSummationMaxMessages: Int,

    @ColumnInfo(name = "sticky_facts_max_messages")
    val stickyFactsMaxMessages: Int,

    @ColumnInfo(name = "api_key")
    val apiKey: String,

    @ColumnInfo(name = "base_url")
    val baseUrl: String,

    @ColumnInfo(name = "base_model")
    val baseModel: String,
) {
    companion object {
        /**
         * Идентификатор singleton-записи в таблице настроек.
         * Гарантирует, что в базе будет не более одной строки с этим ключом.
         */
        const val SINGLETON_ID: Int = 1
    }
}
