package com.github.mobdev778.aiadventchallenge.data.chat.datasource

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.github.mobdev778.aiadventchallenge.data.chat.datasource.model.ChatEntity
import com.github.mobdev778.aiadventchallenge.data.chat.datasource.model.ChatMessageEntity
import com.github.mobdev778.aiadventchallenge.data.chat.datasource.model.StickyFactsEntity
import java.nio.file.Path

/**
 * База данных Room для приложения чата, содержащая сущности:
 * - [ChatEntity] — информация о чатах,
 * - [ChatMessageEntity] — сообщения внутри чатов,
 * - [StickyFactsEntity] — записи об обработанных сообщениях с закреплёнными фактами.
 *
 * Предоставляет DAO-объекты:
 * - [chatDao] для работы с чатами и сообщениями,
 * - [stickyFactsDao] для отслеживания обработанных фактов.
 *
 * Версия базы данных — 9. Экспорт схемы отключён.
 * На чистой JVM используется bundled-драйвер [BundledSQLiteDriver].
 * В случае несовместимости версий применяется деструктивная миграция с удалением всех таблиц.
 */
@Database(
    entities = [
        ChatEntity::class,
        ChatMessageEntity::class,
        StickyFactsEntity::class,
    ],
    version = 9,
    exportSchema = false,
)
abstract class ChatAppDatabase : RoomDatabase() {

    /**
     * Возвращает DAO для операций с чатами и сообщениями.
     */
    abstract fun chatDao(): ChatDao

    /**
     * Возвращает DAO для работы с таблицей обработанных липких фактов.
     */
    abstract fun stickyFactsDao(): StickyFactsDao

    companion object {
        /**
         * Создаёт и возвращает экземпляр [ChatAppDatabase] с файлом базы данных по указанному пути.
         *
         * На чистой JVM обязательно явно задаётся SQLite-драйвер ([BundledSQLiteDriver]).
         * При изменении схемы, несовместимом с существующей базой, все таблицы удаляются
         * и создаются заново ([fallbackToDestructiveMigration]).
         *
         * @param dbFile путь к файлу базы данных на диске
         * @return готовый экземпляр базы данных
         */
        fun create(dbFile: Path): ChatAppDatabase {
            // Room on pure JVM requires an explicit SQLiteDriver.
            // We use the bundled driver (androidx.sqlite:sqlite-bundled).
            return Room.databaseBuilder<ChatAppDatabase>(
                name = dbFile.toAbsolutePath().toString(),
            )
                .setDriver(BundledSQLiteDriver())
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
        }
    }
}
