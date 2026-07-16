package com.github.mobdev778.aiadventchallenge.data.rag.datasource.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Сущность базы данных Room, представляющая фрагмент (чанк) документа, используемого в RAG-системе.
 *
 * Каждый фрагмент содержит часть текста документа, его порядковый номер (section), а также векторное
 * представление (embedding) для семантического поиска. Связан с родительским документом через внешний ключ.
 *
 * @property id Уникальный идентификатор фрагмента (чанка).
 * @property documentId Идентификатор родительского документа, к которому относится данный фрагмент.
 * @property section Порядковый номер фрагмента внутри документа, отражающий его положение.
 * @property text Текстовое содержимое фрагмента.
 * @property vector Строковое представление векторного эмбеддинга данного фрагмента для поиска по сходству.
 */
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
