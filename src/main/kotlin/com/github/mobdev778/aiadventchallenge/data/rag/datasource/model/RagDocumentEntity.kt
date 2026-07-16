package com.github.mobdev778.aiadventchallenge.data.rag.datasource.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сущность базы данных Room, представляющая документ, используемый в системе Retrieval-Augmented Generation (RAG).
 *
 * Содержит основные метаданные документа: уникальный идентификатор, источник и заголовок.
 * Используется для сохранения и извлечения информации о документах, прошедших индексацию для последующего поиска и генерации ответов.
 *
 * @property id Уникальный идентификатор документа, выступающий в роли первичного ключа.
 * @property source Источник, из которого был получен документ (например, URL или путь к файлу).
 * @property title Заголовок или краткое описание документа.
 */
@Entity(tableName = "rag_documents")
data class RagDocumentEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "source")
    val source: String,
    @ColumnInfo(name = "title")
    val title: String,
)
