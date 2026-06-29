package com.github.mobdev778.aiadventchallenge.data.rag.datasource.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rag_documents")
data class RagDocumentEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "source")
    val source: String,
    @ColumnInfo(name = "title")
    val title: String,
)
