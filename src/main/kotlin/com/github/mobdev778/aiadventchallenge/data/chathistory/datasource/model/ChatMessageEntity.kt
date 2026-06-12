package com.github.mobdev778.aiadventchallenge.data.chathistory.datasource.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey
    val id: UUID,

    @ColumnInfo(name = "parent_id")
    val parentId: UUID?,

    @ColumnInfo(name = "created_at_millis")
    val createdAtMillis: Long,

    @ColumnInfo(name = "text")
    val text: String,

    @ColumnInfo(name = "author")
    val author: ChatAuthorEntity,

    @ColumnInfo(name = "tokens")
    val tokens: Int,

    @ColumnInfo(name = "rank")
    val rank: Int,
)

