package com.github.mobdev778.aiadventchallenge.data.settings.repository

import com.github.mobdev778.aiadventchallenge.data.settings.datasource.SettingsDao
import com.github.mobdev778.aiadventchallenge.data.settings.datasource.model.SettingsEntity
import com.github.mobdev778.aiadventchallenge.domain.messageselection.MessageSelectionType
import com.github.mobdev778.aiadventchallenge.domain.settings.model.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
class SettingsRepository(
    private val settingsDao: SettingsDao,
) {

    fun observeSettings(): Flow<AppSettings> =
        settingsDao.observeById(SettingsEntity.SINGLETON_ID)
            .map { entity -> entity?.toDomain() ?: defaultSettings() }
            .distinctUntilChanged()

    /**
     * Synchronous accessor for places where DI graph needs a value (e.g. OkHttp/Retrofit factories).
     * The cache is updated from [observeSettings] and [updateSettings].
     */
    suspend fun getSettings(): AppSettings {
        val settingsEntity: SettingsEntity? = settingsDao.getById(SettingsEntity.SINGLETON_ID)
        return settingsEntity?.toDomain() ?: defaultSettings()
    }

    suspend fun updateSettings(settings: AppSettings) {
        settingsDao.upsert(settings.toEntity())
    }

    private fun SettingsEntity.toDomain(): AppSettings {
        val type = runCatching { MessageSelectionType.valueOf(messageSelectionType) }
            .getOrDefault(MessageSelectionType.FullHistory)

        return AppSettings(
            messageSelectionType = type,
            maxMessages = maxMessages,
            maxTokens = maxTokens,
            apiKey = apiKey,
            baseUrl = baseUrl,
            baseModel = baseModel,
        )
    }

    private fun AppSettings.toEntity(): SettingsEntity =
        SettingsEntity(
            id = SettingsEntity.SINGLETON_ID,
            messageSelectionType = messageSelectionType.name,
            maxMessages = maxMessages,
            maxTokens = maxTokens,
            apiKey = apiKey,
            baseUrl = baseUrl,
            baseModel = baseModel,
        )

    private fun defaultSettings(): AppSettings =
        AppSettings(
            messageSelectionType = MessageSelectionType.FullHistory,
            maxMessages = 20,
            maxTokens = 4096,
            apiKey = "",
            baseUrl = "http://127.0.0.1:1234",
            baseModel = "qwen/qwen3-14b",
        )

}
