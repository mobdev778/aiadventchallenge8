package com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.model

import java.util.UUID

/**
 * Модель элемента списка RAG-документов, используемая на экране отображения документов
 * Retrieval-Augmented Generation (RAG) системы.
 *
 * Представляет собой неизменяемый контейнер данных, содержащий ключевую информацию
 * о документе: его уникальный идентификатор, источник происхождения, заголовок
 * и количество фрагментов (чанков), на которые разбит документ для индексации.
 *
 * @property id Уникальный идентификатор документа в системе.
 * @property source Источник или происхождение документа (например, название файла, URL или имя хранилища).
 * @property title Заголовок документа, отображаемый пользователю в списке.
 * @property chunkCount Количество фрагментов (чанков), на которые разбит документ для семантического поиска.
 */
data class RagDocumentListItem(
    val id: UUID,
    val source: String,
    val title: String,
    val chunkCount: Int,
)
