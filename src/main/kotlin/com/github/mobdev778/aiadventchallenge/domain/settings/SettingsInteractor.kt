package com.github.mobdev778.aiadventchallenge.domain.settings

import com.github.mobdev778.aiadventchallenge.data.settings.repository.SettingsRepository
import com.github.mobdev778.aiadventchallenge.domain.settings.model.AppSettings
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single
class SettingsInteractor(
    val repository: SettingsRepository,
) {

    fun observeSettings(): Flow<AppSettings> {
        return repository.observeSettings()
    }

    suspend fun update(settings: AppSettings) {
        repository.updateSettings(settings)
    }
}