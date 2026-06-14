package com.github.mobdev778.aiadventchallenge.data.chat.repository

import com.github.mobdev778.aiadventchallenge.data.chat.datasource.ChatDao
import com.github.mobdev778.aiadventchallenge.data.chat.datasource.model.ChatEntity
import com.github.mobdev778.aiadventchallenge.data.chat.datasource.model.ChatMessageEntity
import com.github.mobdev778.aiadventchallenge.data.chat.datasource.model.MessageTypeEntity
import com.github.mobdev778.aiadventchallenge.domain.chat.model.Chat
import com.github.mobdev778.aiadventchallenge.domain.chat.model.ChatMessage
import com.github.mobdev778.aiadventchallenge.domain.chat.model.MessageType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single
import java.util.UUID

@Single
class ChatRepository(
    private val chatDao: ChatDao,
) {
    fun observeChats(): Flow<List<Chat>> =
        chatDao.observeChats().map { entities ->
            entities.map { it.toDomain() }
        }

    fun observeChat(chatId: UUID): Flow<Chat?> =
        chatDao.observeChat(chatId).map { it?.toDomain() }

    fun observeMessages(chatId: UUID): Flow<List<ChatMessage>> =
        chatDao.observeMessages(chatId).map { entities ->
            entities
                .sortedBy { it.createdAtMillis }
                .map { it.toDomain() }
        }

    suspend fun clearMessages(chatId: UUID) {
        chatDao.clearMessages(chatId)
    }

    suspend fun createChat(chat: Chat) {
        chatDao.insertChat(chat.toEntity())
    }

    suspend fun deleteChat(chatId: UUID) {
        // ensure messages are removed too (no FK cascade configured)
        chatDao.deleteMessagesByChatId(chatId)
        chatDao.deleteChatById(chatId)
    }

    suspend fun add(message: ChatMessage) {
        chatDao.insertMessage(message.toEntity())
    }

    suspend fun add(messages: List<ChatMessage>) {
        chatDao.insertMessagesWithTransaction(messages.map { it.toEntity() })
    }

    suspend fun delete(message: ChatMessage) {
        chatDao.deleteMessageById(message.id)
    }

    private fun ChatEntity.toDomain(): Chat =
        Chat(
            id = id,
            name = name,
            time = createdAtMillis,
            parentId = parentId,
        )

    private fun Chat.toEntity(): ChatEntity =
        ChatEntity(
            id = id,
            name = name,
            createdAtMillis = time,
            parentId = parentId,
        )

    private fun ChatMessageEntity.toDomain(): ChatMessage =
        ChatMessage(
            id = id,
            chatId = chatId,
            parentId = parentId,
            time = createdAtMillis,
            text = text,
            branchB = branchB,
            type = when (type) {
                MessageTypeEntity.User -> MessageType.User
                MessageTypeEntity.Assistant -> MessageType.Bot
                MessageTypeEntity.StickyFacts -> MessageType.StickyFacts
            },
            tokens = tokens,
            rank = rank,
        )

    private fun ChatMessage.toEntity(): ChatMessageEntity =
        ChatMessageEntity(
            id = id,
            chatId = chatId,
            parentId = parentId,
            createdAtMillis = time,
            text = text,
            branchB = branchB,
            type = when (type) {
                MessageType.User -> MessageTypeEntity.User
                MessageType.Bot -> MessageTypeEntity.Assistant
                MessageType.StickyFacts -> MessageTypeEntity.StickyFacts
            },
            tokens = tokens,
            rank = rank,
        )
}
