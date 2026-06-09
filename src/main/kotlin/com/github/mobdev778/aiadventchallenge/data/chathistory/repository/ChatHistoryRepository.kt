package com.github.mobdev778.aiadventchallenge.data.chathistory.repository

import com.github.mobdev778.aiadventchallenge.data.chathistory.datasource.ChatDao
import com.github.mobdev778.aiadventchallenge.data.chathistory.datasource.model.ChatAuthorEntity
import com.github.mobdev778.aiadventchallenge.data.chathistory.datasource.model.ChatMessageEntity
import com.github.mobdev778.aiadventchallenge.domain.chathistory.ChatAuthor
import com.github.mobdev778.aiadventchallenge.domain.chathistory.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatHistoryRepository(
    private val chatDao: ChatDao,
) {
    fun observe(): Flow<List<ChatMessage>> =
        chatDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }

    suspend fun clear() {
        chatDao.clear()
    }

    suspend fun addMessage(message: ChatMessage) {
        chatDao.insert(message.toEntity())
    }

    private fun ChatMessageEntity.toDomain(): ChatMessage =
        ChatMessage(
            id = id,
            text = text,
            author = when (author) {
                ChatAuthorEntity.USER -> ChatAuthor.User
                ChatAuthorEntity.ASSISTANT -> ChatAuthor.Bot
            },
        )

    private fun ChatMessage.toEntity(): ChatMessageEntity =
        ChatMessageEntity(
            id = id,
            text = text,
            author = when (author) {
                ChatAuthor.User -> ChatAuthorEntity.USER
                ChatAuthor.Bot -> ChatAuthorEntity.ASSISTANT
            },
            createdAtMillis = System.currentTimeMillis(),
        )
}
