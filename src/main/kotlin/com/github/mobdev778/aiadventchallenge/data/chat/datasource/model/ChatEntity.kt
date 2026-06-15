package com.github.mobdev778.aiadventchallenge.data.chat.datasource.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

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
