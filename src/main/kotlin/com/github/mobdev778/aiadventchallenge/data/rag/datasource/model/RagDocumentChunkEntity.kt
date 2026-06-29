package com.github.mobdev778.aiadventchallenge.data.rag.datasource.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "rag_document_chunks",
    foreignKeys = [
        ForeignKey(
            entity = RagDocumentEntity::class,
            parentColumns = ["id"],
            childColumns = ["document_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["document_id"])],
)
data class RagDocumentChunkEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "document_id")
    val documentId: String,
    @ColumnInfo(name = "section")
    val section: Int,
    @ColumnInfo(name = "text")
    val text: String,
    @ColumnInfo(name = "vector")
    val vector: String,
)
