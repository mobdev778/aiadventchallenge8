package com.github.mobdev778.aiadventchallenge.data.taskcontext.datasource

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.github.mobdev778.aiadventchallenge.data.taskcontext.datasource.model.TaskContextEntity
import java.nio.file.Path

/**
 * Локальная база данных Room для хранения контекстов задач AI-агента.
 *
 * Содержит единственную таблицу `task_context`, основанную на сущности [TaskContextEntity].
 * Предоставляет DAO-интерфейс [TaskContextDao] для наблюдаемого и прямого доступа к записям.
 *
 * Класс является абстрактным, Room генерирует его конкретную реализацию во время компиляции.
 * Фабрика, расположенная в объекте-компаньоне, позволяет создать экземпляр базы данных,
 * размещённой по указанному пути файловой системы, с использованием встроенного SQLite-драйвера.
 */
@Database(
    entities = [TaskContextEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class TaskContextAppDatabase : RoomDatabase() {

    /**
     * Возвращает реализованный Room DAO для работы с контекстами задач.
     *
     * @return экземпляр [TaskContextDao], предоставляющий методы запросов и манипуляции данными.
     */
    abstract fun taskContextDao(): TaskContextDao

    companion object {
        /**
         * Фабричный метод создания базы данных в заданном файле.
         *
         * Настраивает построитель Room с драйвером [BundledSQLiteDriver] и политикой деструктивной миграции:
         * при повышении версии БД без предоставленного миграционного пути все таблицы удаляются и создаются заново.
         *
         * @param dbFile путь к файлу базы данных (абсолютный или относительный).
         * @return готовый экземпляр [TaskContextAppDatabase].
         */
        fun create(dbFile: Path): TaskContextAppDatabase {
            return Room.databaseBuilder<TaskContextAppDatabase>(
                name = dbFile.toAbsolutePath().toString(),
            )
                .setDriver(BundledSQLiteDriver())
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
        }
    }
}
