package com.github.mobdev778.aiadventchallenge.data.chat.datasource.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

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
)

