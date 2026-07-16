package com.github.mobdev778.aiadventchallenge.data.chat.datasource.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Сущность комнаты (Room) для хранения информации о чате.
 *
 * Чаты представляют собой ветвящиеся диалоги: каждый чат может иметь родительский чат
 * ([parentId]), что позволяет организовывать иерархию обсуждений. Кроме того, чат может
 * быть привязан к определённому контексту задачи ([taskContextId]).
 *
 * @property id Уникальный идентификатор чата.
 * @property name Отображаемое имя чата.
 * @property createdAtMillis Время создания чата в миллисекундах (Unix epoch).
 * @property parentId Идентификатор родительского чата, либо `null`, если чат является корневым.
 * @property taskContextId Идентификатор контекста задачи, к которой привязан чат, либо `null`,
 *   если чат не связан с конкретной задачей.
 */
@Entity(
    tableName = "chats",
    indices = [
        Index(value = ["parent_id"]),
        Index(value = ["created_at_millis"]),
        Index(value = ["task_context_id"]),
    ],
)
data class ChatEntity(
    @PrimaryKey
    val id: UUID,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "created_at_millis")
    val createdAtMillis: Long,

    @ColumnInfo(name = "parent_id")
    val parentId: UUID?,

    @ColumnInfo(name = "task_context_id")
    val taskContextId: UUID?,
)
