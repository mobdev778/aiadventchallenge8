package com.github.mobdev778.aiadventchallenge.data.settings.datasource

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.github.mobdev778.aiadventchallenge.data.settings.datasource.model.SettingsEntity
import java.nio.file.Path

/**
 * Локальная база данных Room для приложения, предназначенная для хранения singleton-записи
 * с настройками.
 *
 * База содержит единственную таблицу, определённую сущностью [SettingsEntity]. Для обеспечения
 * единственности строки настроек в таблице используется паттерн singleton-записи с фиксированным
 * первичным ключом, равным [SettingsEntity.SINGLETON_ID].
 *
 * В случае изменения схемы (повышения версии) без предоставления миграции применяется
 * [fallbackToDestructiveMigration] – это приводит к удалению существующей таблицы и её
 * пересозданию (`dropAllTables = true`), что допустимо для таблицы, содержащей только
 * пользовательские настройки, которые могут быть восстановлены повторным вводом.
 *
 * @see SettingsEntity
 * @see SettingsDao
 */
@Database(
    entities = [SettingsEntity::class],
    version = 4,
    exportSchema = false,
)
abstract class SettingsAppDatabase : RoomDatabase() {

    /**
     * Предоставляет экземпляр [SettingsDao] для выполнения операций чтения и записи
     * над singleton-записью настроек.
     *
     * Все методы DAO (например, [SettingsDao.observeById], [SettingsDao.getById])
     * оперируют единственной записью с идентификатором [SettingsEntity.SINGLETON_ID].
     *
     * @return интерфейс доступа к данным настроек.
     */
    abstract fun settingsDao(): SettingsDao

    companion object {
        /**
         * Создаёт и настраивает экземпляр [SettingsAppDatabase], использующий файл базы данных
         * по указанному пути.
         *
         * В качестве SQLite-драйвера применяется [BundledSQLiteDriver]. При несовпадении версий
         * схемы (без явной миграции) включено разрушающее обновление с удалением всех таблиц,
         * чтобы избежать исключений миграции.
         *
         * @param dbFile Путь к файлу базы данных на локальной файловой системе.
         * @return Готовый к использованию синглтон (в рамках вызова) [SettingsAppDatabase].
         */
        fun create(dbFile: Path): SettingsAppDatabase {
            return Room.databaseBuilder<SettingsAppDatabase>(
                name = dbFile.toAbsolutePath().toString(),
            )
                .setDriver(BundledSQLiteDriver())
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
        }
    }
}
