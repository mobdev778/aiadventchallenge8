package com.github.mobdev778.aiadventchallenge.data.profile.datasource

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.github.mobdev778.aiadventchallenge.data.profile.datasource.model.ProfileEntity
import java.nio.file.Path

/**
 * База данных Room для работы с профилями.
 *
 * Определяет структуру локального хранилища профилей на основе одной сущности [ProfileEntity],
 * предоставляет доступ к объекту DAO [ProfileDao] и настраивает параметры миграции и драйвера.
 *
 * Используется для создания экземпляра базы данных с файловым хранилищем по заданному пути.
 */
@Database(
    entities = [ProfileEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class ProfileAppDatabase : RoomDatabase() {

    /**
     * Возвращает реализацию [ProfileDao] для выполнения операций с профилями.
     *
     * @return объект доступа к данным профилей
     */
    abstract fun profileDao(): ProfileDao

    companion object {

        /**
         * Создаёт экземпляр [ProfileAppDatabase] по указанному файловому пути.
         *
         * Настройки сборщика:
         * - используется драйвер [BundledSQLiteDriver] из пакета библиотеки Room;
         * - при несовместимости схем применяется деструктивная миграция
         *   (все таблицы удаляются и создаются заново).
         *
         * @param dbFile путь к файлу базы данных (абсолютный или относительный)
         * @return готовый экземпляр базы данных
         */
        fun create(dbFile: Path): ProfileAppDatabase {
            return Room.databaseBuilder<ProfileAppDatabase>(
                name = dbFile.toAbsolutePath().toString(),
            )
                .setDriver(BundledSQLiteDriver())
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
        }
    }
}
