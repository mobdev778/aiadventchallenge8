package com.github.mobdev778.aiadventchallenge.data.settings.repository

import com.github.mobdev778.aiadventchallenge.data.settings.datasource.SettingsDao
import com.github.mobdev778.aiadventchallenge.data.settings.datasource.model.SettingsEntity
import com.github.mobdev778.aiadventchallenge.domain.settings.model.AppSettings
import com.github.mobdev778.aiadventchallenge.domain.settings.model.ContextManagementType
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
            .map { entity -> entity?.toDomain() ?: default }
            .distinctUntilChanged()

    /**
     * Synchronous accessor for places where DI graph needs a value (e.g. OkHttp/Retrofit factories).
     * The cache is updated from [observeSettings] and [updateSettings].
     */
    suspend fun getSettings(): AppSettings {
        val settingsEntity: SettingsEntity? = settingsDao.getById(SettingsEntity.SINGLETON_ID)
        return settingsEntity?.toDomain() ?: default
    }

    suspend fun updateSettings(settings: AppSettings) {
        settingsDao.upsert(settings.toEntity())
    }

    private fun SettingsEntity.toDomain(): AppSettings {
        val contextManagementType = runCatching { ContextManagementType.valueOf(this.contextManagementType) }
            .getOrDefault(ContextManagementType.None)

        return AppSettings(
            contextManagementType = contextManagementType,
            maxMessages = maxMessages,
            maxTokens = maxTokens,
            recursiveSummationMaxMessages = recursiveSummationMaxMessages,
            stickyFactsMaxMessages = stickyFactsMaxMessages,
            apiKey = apiKey,
            baseUrl = baseUrl,
            baseModel = baseModel.trim().replace("\n", ""),
        )
    }

    private fun AppSettings.toEntity(): SettingsEntity =
        SettingsEntity(
            id = SettingsEntity.SINGLETON_ID,
            contextManagementType = contextManagementType.name,
            maxMessages = maxMessages,
            maxTokens = maxTokens,
            recursiveSummationMaxMessages = recursiveSummationMaxMessages,
            stickyFactsMaxMessages = stickyFactsMaxMessages,
            apiKey = apiKey,
            baseUrl = baseUrl,
            baseModel = baseModel,
        )

    companion object {
        val default = AppSettings(
            contextManagementType = ContextManagementType.None,
            maxMessages = 6,
            maxTokens = 4096,
            recursiveSummationMaxMessages = 6,
            stickyFactsMaxMessages = 6,
            apiKey = "",
            baseUrl = "https://api.proxyapi.ru/openai/v1",
            baseModel = "gpt-5.2",
        )
    }
}
