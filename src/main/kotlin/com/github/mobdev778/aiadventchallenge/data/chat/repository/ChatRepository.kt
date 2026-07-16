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

/**
 * Репозиторий для работы с чатами и сообщениями в слое данных.
 *
 * Предоставляет методы для наблюдения за сущностями чатов и сообщений,
 * а также для их добавления, удаления и очистки. Репозиторий инкапсулирует
 * логику преобразования между доменными моделями ([Chat], [ChatMessage])
 * и сущностями базы данных ([ChatEntity], [ChatMessageEntity]), используя
 * [ChatDao] в качестве источника данных.
 *
 * Аннотирован как [Single] для интеграции с Koin.
 */
@Suppress("TooManyFunctions")
@Single
class ChatRepository(
    private val chatDao: ChatDao,
) {
    /**
     * Возвращает [Flow] со списком всех чатов, отсортированных по убыванию времени создания.
     *
     * @return [Flow], эмитирующий актуальный список [Chat] при каждом изменении в источнике данных.
     */
    fun observeChats(): Flow<List<Chat>> =
        chatDao.observeChats().map { entities ->
            entities.map { it.toDomain() }
        }

    /**
     * Наблюдает за конкретным чатом по его идентификатору.
     *
     * @param chatId Уникальный идентификатор чата.
     * @return [Flow], эмитирующий экземпляр [Chat] или `null`, если чат не найден.
     */
    fun observeChat(chatId: UUID): Flow<Chat?> =
        chatDao.observeChat(chatId).map { it?.toDomain() }

    /**
     * Наблюдает за всеми сообщениями заданного чата.
     *
     * Сообщения сортируются по возрастанию времени создания.
     *
     * @param chatId Идентификатор чата.
     * @return [Flow], эмитирующий актуальный список [ChatMessage] чата.
     */
    fun observeMessages(chatId: UUID): Flow<List<ChatMessage>> =
        chatDao.observeMessages(chatId).map { entities ->
            entities
                .sortedBy { it.createdAtMillis }
                .map { it.toDomain() }
        }

    /**
     * Удаляет все сообщения из указанного чата.
     *
     * @param chatId Идентификатор чата, который необходимо очистить.
     */
    suspend fun clearMessages(chatId: UUID) {
        chatDao.clearMessages(chatId)
    }

    /**
     * Добавляет или обновляет чат в базе данных.
     *
     * @param chat Доменная модель чата для сохранения.
     */
    suspend fun add(chat: Chat) {
        chatDao.insertChat(chat.toEntity())
    }

    /**
     * Удаляет чат и все связанные с ним сообщения.
     *
     * @param chatId Идентификатор удаляемого чата.
     */
    suspend fun deleteChat(chatId: UUID) {
        // ensure messages are removed too (no FK cascade configured)
        chatDao.deleteMessagesByChatId(chatId)
        chatDao.deleteChatById(chatId)
    }

    /**
     * Сохраняет отдельное сообщение в базу данных.
     *
     * @param message Доменная модель сообщения для сохранения.
     */
    suspend fun add(message: ChatMessage) {
        chatDao.insertMessage(message.toEntity())
    }

    /**
     * Сохраняет список сообщений в рамках одной транзакции.
     *
     * @param messages Список доменных моделей сообщений для сохранения.
     */
    suspend fun add(messages: List<ChatMessage>) {
        chatDao.insertMessagesWithTransaction(messages.map { it.toEntity() })
    }

    /**
     * Удаляет конкретное сообщение по его идентификатору.
     *
     * @param message Доменная модель сообщения, подлежащего удалению.
     */
    suspend fun delete(message: ChatMessage) {
        chatDao.deleteMessageById(message.id)
    }

    private fun ChatEntity.toDomain(): Chat =
        Chat(
            id = id,
            name = name,
            time = createdAtMillis,
            parentId = parentId,
            taskContextId = taskContextId,
        )

    private fun Chat.toEntity(): ChatEntity =
        ChatEntity(
            id = id,
            name = name,
            createdAtMillis = time,
            parentId = parentId,
            taskContextId = taskContextId,
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
                MessageTypeEntity.Tool -> MessageType.Tool
                MessageTypeEntity.StickyFacts -> MessageType.StickyFacts
            },
            tokens = tokens,
            rank = rank,
            toolCallId = toolCallId,
            name = name,
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
                MessageType.Tool -> MessageTypeEntity.Tool
                MessageType.StickyFacts -> MessageTypeEntity.StickyFacts
            },
            tokens = tokens,
            rank = rank,
            toolCallId = toolCallId,
            name = name,
        )
}
