package com.github.mobdev778.aiadventchallenge.data.chat.datasource.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Сущность сообщения в чате, представляющая запись в таблице "chat_messages".
 *
 * Каждое сообщение привязано к конкретному чату ([chatId]), может иметь родительское
 * сообщение ([parentId]) для построения древовидной структуры диалога и ветвления ([branchB]).
 *
 * Содержит текст сообщения, его тип (см. [MessageTypeEntity]), количество потреблённых
 * токенов и ранг, определяющий порядок в интерфейсе. Поле [toolCallId] используется
 * для связи сообщений типа [MessageTypeEntity.Tool] с вызванными функциями.
 *
 * Индексы:
 * - по полю `chat_id` для быстрой выборки сообщений чата
 * - по полю `parent_id` для эффективного поиска дочерних сообщений.
 *
 * @property id Уникальный идентификатор сообщения.
 * @property chatId Идентификатор чата, к которому относится сообщение.
 * @property parentId Идентификатор родительского сообщения (null для корневых сообщений).
 * @property createdAtMillis Время создания сообщения в миллисекундах (Unix timestamp).
 * @property branchB Флаг, указывающий, что сообщение находится в ветке B (альтернативный ответ).
 * @property text Текстовое содержимое сообщения.
 * @property type Тип сообщения: пользователь, ассистент, вызов инструмента или закреплённый факт.
 * @property tokens Количество токенов, затраченных на сообщение (0 для пользовательских сообщений).
 * @property rank Порядковый номер отображения сообщения в интерфейсе чата.
 * @property toolCallId Идентификатор вызова инструмента, если сообщение связано с [MessageTypeEntity.Tool].
 * @property name Имя пользователя или ассистента (опционально, например, для displayName).
 */
@Entity(
    tableName = "chat_messages",
    indices = [
        Index(value = ["chat_id"]),
        Index(value = ["parent_id"]),
    ],
)
data class ChatMessageEntity(
    @PrimaryKey
    val id: UUID,

    @ColumnInfo(name = "chat_id")
    val chatId: UUID,

    @ColumnInfo(name = "parent_id")
    val parentId: UUID?,

    @ColumnInfo(name = "created_at_millis")
    val createdAtMillis: Long,

    @ColumnInfo(name = "branch_b")
    val branchB: Boolean,

    @ColumnInfo(name = "text")
    val text: String,

    @ColumnInfo(name = "type")
    val type: MessageTypeEntity,

    @ColumnInfo(name = "tokens")
    val tokens: Int,

    @ColumnInfo(name = "rank")
    val rank: Int,

    @ColumnInfo(name = "tool_call_id")
    val toolCallId: String? = null,

    @ColumnInfo(name = "name")
    val name: String? = null,
)
