package com.github.mobdev778.aiadventchallenge.data.settings.datasource

import com.intellij.openapi.application.PathManager
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import java.nio.file.Files
import java.nio.file.Path

@Module
class SettingsDatabaseModule {

    @Single
    fun provideSettingsAppDatabase(): SettingsAppDatabase {
        val systemPath = PathManager.getSystemDir()
        val dir: Path = systemPath.resolve("aiadventchallenge")
        Files.createDirectories(dir)
        val dbFile = dir.resolve("settings.db")
        return SettingsAppDatabase.create(dbFile)
    }

    @Single
    fun provideSettingsDao(settingsAppDatabase: SettingsAppDatabase): SettingsDao {
        return settingsAppDatabase.settingsDao()
    }
}