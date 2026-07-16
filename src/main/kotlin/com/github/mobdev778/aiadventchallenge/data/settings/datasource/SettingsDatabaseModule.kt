package com.github.mobdev778.aiadventchallenge.data.settings.datasource

import com.intellij.openapi.application.PathManager
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import java.nio.file.Files
import java.nio.file.Path

/**
 * Koin‑модуль, отвечающий за предоставление зависимостей для слоя данных настроек приложения.
 *
 * Регистрирует singleton‑объекты:
 * - [SettingsAppDatabase] — локальная база данных Room, хранящаяся в файле `settings.db`
 *   в подкаталоге `aiadventchallenge` внутри системной директории IntelliJ;
 * - [SettingsDao] — интерфейс доступа к таблице настроек.
 *
 * Расположение файла базы данных определяется с помощью [PathManager.getSystemDir].
 */
@Module
class SettingsDatabaseModule {

    /**
     * Создаёт и предоставляет singleton‑экземпляр [SettingsAppDatabase], хранящийся в файле
     * `settings.db` внутри подкаталога `aiadventchallenge` системной директории плагина.
     *
     * Каталог при необходимости создаётся автоматически через [Files.createDirectories].
     *
     * @return Экземпляр локальной базы данных Room, готовый к использованию.
     */
    @Single
    fun provideSettingsAppDatabase(): SettingsAppDatabase {
        val systemPath = PathManager.getSystemDir()
        val dir: Path = systemPath.resolve("aiadventchallenge")
        Files.createDirectories(dir)
        val dbFile = dir.resolve("settings.db")
        return SettingsAppDatabase.create(dbFile)
    }

    /**
     * Предоставляет singleton‑реализацию [SettingsDao], связанную с переданной базой данных.
     *
     * @param settingsAppDatabase Экземпляр базы данных, из которого будет извлечён DAO.
     * @return Интерфейс доступа к настройкам ([SettingsDao]), настроенный для работы с переданной БД.
     */
    @Single
    fun provideSettingsDao(settingsAppDatabase: SettingsAppDatabase): SettingsDao {
        return settingsAppDatabase.settingsDao()
    }
}
