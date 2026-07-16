package com.github.mobdev778.aiadventchallenge.data.rag.datasource.model

import kotlinx.serialization.Serializable

/**
 * DTO-объект, представляющий сообщение чата в подсистеме RAG (Retrieval-Augmented Generation).
 *
 * Используется для сериализации/десериализации сообщений при обмене данными между клиентом
 * и серверной частью, отвечающей за генерацию ответов с привлечением контекстных документов.
 *
 * @property text Текст сообщения на языке оригинала, полученный от пользователя.
 * @property english Текст сообщения, переведённый на английский язык для дальнейшей обработки.
 * @property long Временная метка отправки сообщения (в миллисекундах от начала эпохи).
 * @property role Роль отправителя сообщения (например, "user" или "assistant").
 */
@Serializable
data class RagChatMessageDto(
    val text: String,
    val english: String,
    val time: Long,
    val role: String,
)
