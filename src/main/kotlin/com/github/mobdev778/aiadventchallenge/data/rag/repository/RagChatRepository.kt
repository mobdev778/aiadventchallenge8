package com.github.mobdev778.aiadventchallenge.data.rag.repository

import com.github.mobdev778.aiadventchallenge.data.chat.repository.ChatRepository
import com.github.mobdev778.aiadventchallenge.data.rag.datasource.model.RagChatMessageDto
import com.github.mobdev778.aiadventchallenge.domain.chat.model.Chat
import com.github.mobdev778.aiadventchallenge.domain.chat.model.ChatMessage
import com.github.mobdev778.aiadventchallenge.domain.chat.model.MessageType
import com.github.mobdev778.aiadventchallenge.domain.mymcpserver.rag.RagRussianFilter
import com.github.mobdev778.aiadventchallenge.domain.rag.RagChunkGenerator
import com.github.mobdev778.aiadventchallenge.domain.rag.RagSearcher
import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagDocument
import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagDocumentChunk
import com.github.mobdev778.aiadventchallenge.domain.rag.ranker.RankerFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single
import java.util.UUID
import kotlin.collections.first
import kotlin.collections.firstOrNull
import kotlin.collections.map

@Single
class RagChatRepository(
    private val chatRepository: ChatRepository,
    private val documentRepository: RagDocumentRepository,
    private val simpleRagSearcher: RagSearcher,
    private val json: Json,
    private val rankerFactory: RankerFactory,
    private val ragRussianFilter: RagRussianFilter,
) {

    fun observeMessages(chatId: UUID): Flow<List<ChatMessage>> {
        return chatRepository.observeMessages(chatId)
    }

    fun observeChats(): Flow<List<Chat>> {
        return chatRepository.observeChats()
    }

    fun observeChat(chatId: UUID): Flow<Chat?> {
        return chatRepository.observeChat(chatId)
    }

    suspend fun add(chat: Chat) {
        chatRepository.add(chat)
        documentRepository.createDocument(
            RagDocument(
                id = chat.id,
                source = "[Chat]: ${chat.name}",
                title = chat.name,
            )
        )
    }

    suspend fun find(chatId: UUID, query: String): List<RagChatMessageDto> {
        val query = ragRussianFilter.filter(query)
        val searchResult = simpleRagSearcher.search(chatId, query, 5).firstOrNull() ?: return emptyList()
        return json.decodeFromString<List<RagChatMessageDto>>(searchResult.text)
    }

    suspend fun clearMessages(chatId: UUID) {
        chatRepository.clearMessages(chatId)
        documentRepository.deleteChunks(chatId)
    }

    suspend fun add(message: ChatMessage) {
        chatRepository.add(message)
        addRagMessage(message)
    }

    suspend fun add(messages: List<ChatMessage>) {
        chatRepository.add(messages)
        addRagGroupMessage(messages)
        messages.forEach {
            addRagMessage(it)
        }
    }

    suspend fun delete(message: ChatMessage) {
        chatRepository.delete(message)
        documentRepository.deleteChunk(message.id)
    }

    suspend fun deleteChat(chatId: UUID) {
        chatRepository.deleteChat(chatId)
        documentRepository.deleteDocument(chatId)
    }

    private suspend fun addRagGroupMessage(messages: List<ChatMessage>) {
        val firstMessage = messages.firstOrNull() ?: return
        val list = messages.map { convertToDto(it) }
        val text = json.encodeToString(list)

        val messageVector = RagChunkGenerator(firstMessage.chatId, rankerFactory.embeddingModel)
            .generate(section = 0, text = text)
            .vector

        val chunk = RagDocumentChunk(
            documentId = messages.first().chatId,
            id = UUID.randomUUID(),
            section = (firstMessage.time / 1000L).toInt(),
            text = text,
            vector = messageVector,
        )

        documentRepository.add(chunk)
    }

    private suspend fun addRagMessage(message: ChatMessage) {
        val list = listOf(convertToDto(message))
        val text = json.encodeToString(list)

        val messageVector = RagChunkGenerator(message.chatId, rankerFactory.embeddingModel)
            .generate(section = 0, text = text)
            .vector

        val chunk = RagDocumentChunk(
            documentId = message.chatId,
            id = message.id,
            section = (message.time / 1000L).toInt(),
            text = text,
            vector = messageVector,
        )
        documentRepository.add(chunk)
    }

    private suspend fun convertToDto(message: ChatMessage): RagChatMessageDto {
        val english = ragRussianFilter.filter(message.text)
        return RagChatMessageDto(
            english = english,
            text = message.text,
            time = message.time,
            role = convertMessageType(message.type),
        )
    }

    private fun convertMessageType(messageType: MessageType): String {
        return when (messageType) {
            MessageType.User -> "user"
            MessageType.Tool -> "tool"
            else -> "assistant"
        }
    }
}
