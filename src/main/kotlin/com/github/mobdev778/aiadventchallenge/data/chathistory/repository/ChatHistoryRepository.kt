package com.github.mobdev778.aiadventchallenge.data.chathistory.repository

import com.github.mobdev778.aiadventchallenge.data.chathistory.datasource.ChatDao
import com.github.mobdev778.aiadventchallenge.data.chathistory.datasource.model.ChatAuthorEntity
import com.github.mobdev778.aiadventchallenge.data.chathistory.datasource.model.ChatMessageEntity
import com.github.mobdev778.aiadventchallenge.domain.chathistory.model.ChatAuthor
import com.github.mobdev778.aiadventchallenge.domain.chathistory.model.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
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

    suspend fun add(message: ChatMessage) {
        chatDao.insert(message.toEntity())
    }

    suspend fun delete(message: ChatMessage) {
        chatDao.deleteById(message.id)
    }

    private fun ChatMessageEntity.toDomain(): ChatMessage =
        ChatMessage(
            id = id,
            text = text,
            author = when (author) {
                ChatAuthorEntity.USER -> ChatAuthor.User
                ChatAuthorEntity.ASSISTANT -> ChatAuthor.Bot
            },
            tokens = tokens,
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
            tokens = tokens,
        )
}
