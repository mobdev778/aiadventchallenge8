package com.github.mobdev778.aiadventchallenge.data.chat.datasource.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.UUID

/**
 * Сущность комнаты (Room) для отслеживания обработанных сообщений липких фактов.
 *
 * Фиксирует факт обработки сообщения с идентификатором [messageId] в конкретном чате [chatId].
 * Используется для обеспечения идемпотентности: повторно обрабатывать уже учтённое сообщение
 * не требуется. Таблица ссылается на [ChatEntity] через внешний ключ, гарантируя целостность
 * данных.
 *
 * @property chatId Идентификатор чата. Ссылается на поле [ChatEntity.id]. Является частью составного первичного ключа.
 * @property messageId Идентификатор обработанного сообщения. Вторая часть составного первичного ключа.
 */
@Entity(
    tableName = "sticky_facts_processed_messages",
    primaryKeys = ["chat_id", "message_id"],
    foreignKeys = [
        ForeignKey(
            entity = ChatEntity::class,
            parentColumns = ["id"],
            childColumns = ["chat_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["chat_id"]),
        Index(value = ["message_id"]),
    ],
)
data class StickyFactsEntity(
    @ColumnInfo(name = "chat_id")
    val chatId: UUID,

    @ColumnInfo(name = "message_id")
    val messageId: UUID,
)
