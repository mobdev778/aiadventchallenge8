package com.github.mobdev778.aiadventchallenge.domain.mymcpserver.rag

import kotlinx.serialization.Serializable

/**
 * DTO, представляющий ответ от сервиса поиска релевантных фрагментов (чанков) в RAG-системе.
 *
 * Содержит информацию о статусе выполнения запроса, дополнительное сообщение (например, ошибка или предупреждение)
 * и список найденных чанков, соответствующих критериям поиска.
 *
 * @property status Статус выполнения запроса (например, "ok", "error", "partial").
 * @property message Дополнительное сообщение, уточняющее статус. Может быть `null`, если сообщение не требуется.
 * @property chunks Список найденных фрагментов результатов поиска. Если ничего не найдено, содержит пустой список.
 */
@Serializable
data class MyMcpRagSearchResponseDto(
    val status: String,
    val message: String? = null,
    val chunks: List<MyMcpRagSearchChunkDto> = emptyList(),
)
