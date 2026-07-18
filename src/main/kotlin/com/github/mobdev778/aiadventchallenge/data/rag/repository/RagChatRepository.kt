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

/**
 * Репозиторий чата с интеграцией RAG (Retrieval-Augmented Generation).
 *
 * Связывает управление чатами и сообщениями с RAG-хранилищем документов и фрагментов.
 * Каждое сообщение чата преобразуется в векторное представление и сохраняется в виде
 * [RagDocumentChunk], что позволяет выполнять семантический поиск по истории диалогов.
 * При создании чата автоматически генерируется соответствующий [RagDocument].
 *
 * @property chatRepository Репозиторий для основных CRUD-операций с чатами и сообщениями.
 * @property documentRepository Репозиторий для управления RAG-документами и их фрагментами.
 * @property simpleRagSearcher Сервис семантического поиска по фрагментам документов.
 * @property json Экземпляр [Json] для сериализации/десериализации DTO сообщений.
 * @property rankerFactory Фабрика для получения моделей эмбеддингов, используемых
 *                         при вычислении векторных представлений сообщений.
 * @property ragRussianFilter Фильтр для обработки запросов, содержащих русский текст,
 *                           с возможностью перевода на английский язык.
 */
@Single
@Suppress("TooManyFunctions")
class RagChatRepository(
    private val chatRepository: ChatRepository,
    private val documentRepository: RagDocumentRepository,
    private val simpleRagSearcher: RagSearcher,
    private val json: Json,
    private val rankerFactory: RankerFactory,
    private val ragRussianFilter: RagRussianFilter,
) {

    /**
     * Наблюдает за списком сообщений указанного чата в реальном времени.
     *
     * @param chatId Идентификатор чата, сообщения которого отслеживаются.
     * @return [Flow], эмитирующий актуальный список [ChatMessage] при каждом изменении.
     */
    fun observeMessages(chatId: UUID): Flow<List<ChatMessage>> {
        return chatRepository.observeMessages(chatId)
    }

    /**
     * Наблюдает за списком всех чатов в реальном времени.
     *
     * @return [Flow], эмитирующий актуальный список [Chat] при каждом изменении.
     */
    fun observeChats(): Flow<List<Chat>> {
        return chatRepository.observeChats()
    }

    /**
     * Наблюдает за конкретным чатом в реальном времени.
     *
     * @param chatId Идентификатор отслеживаемого чата.
     * @return [Flow], эмитирующий экземпляр [Chat] или `null`, если чат не найден.
     */
    fun observeChat(chatId: UUID): Flow<Chat?> {
        return chatRepository.observeChat(chatId)
    }

    /**
     * Создаёт новый чат и соответствующий RAG-документ для него.
     *
     * @param chat Доменная модель создаваемого чата.
     */
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

    /**
     * Выполняет семантический поиск по сообщениям чата.
     *
     * Запрос сначала обрабатывается через [ragRussianFilter] для возможного перевода на английский.
     * Затем с помощью [simpleRagSearcher] ищутся наиболее релевантные фрагменты,
     * содержимое которых десериализуется в список [RagChatMessageDto].
     *
     * @param chatId Идентификатор чата, по истории которого производится поиск.
     * @param query Текстовый запрос (может содержать русские символы).
     * @return Список DTO сообщений, релевантных запросу, или пустой список, если результатов нет.
     */
    @Suppress("MagicNumber")
    suspend fun find(chatId: UUID, query: String): List<RagChatMessageDto> {
        val query = ragRussianFilter.filter(query)
        val searchResult = simpleRagSearcher.search(chatId, query, 5).firstOrNull() ?: return emptyList()
        return json.decodeFromString<List<RagChatMessageDto>>(searchResult.text)
    }

    /**
     * Удаляет все сообщения чата и все связанные с ним RAG-фрагменты.
     *
     * @param chatId Идентификатор очищаемого чата.
     */
    suspend fun clearMessages(chatId: UUID) {
        chatRepository.clearMessages(chatId)
        documentRepository.deleteChunks(chatId)
    }

    /**
     * Добавляет новое сообщение в чат и генерирует соответствующий RAG-фрагмент.
     *
     * @param message Доменная модель добавляемого сообщения.
     */
    suspend fun add(message: ChatMessage) {
        chatRepository.add(message)
        addRagMessage(message)
    }

    /**
     * Добавляет список сообщений в чат и генерирует RAG-фрагменты:
     * один общий фрагмент для всей группы и отдельные фрагменты для каждого сообщения.
     *
     * @param messages Список доменных моделей сообщений для добавления.
     */
    suspend fun add(messages: List<ChatMessage>) {
        chatRepository.add(messages)
        addRagGroupMessage(messages)
        messages.forEach {
            addRagMessage(it)
        }
    }

    /**
     * Удаляет сообщение и связанный с ним RAG-фрагмент.
     *
     * @param message Доменная модель удаляемого сообщения.
     */
    suspend fun delete(message: ChatMessage) {
        chatRepository.delete(message)
        documentRepository.deleteChunk(message.id)
    }

    /**
     * Удаляет чат, все его сообщения и связанный RAG-документ.
     *
     * @param chatId Идентификатор удаляемого чата.
     */
    suspend fun deleteChat(chatId: UUID) {
        chatRepository.deleteChat(chatId)
        documentRepository.deleteDocument(chatId)
    }

    @Suppress("TooGenericExceptionCaught", "PrintStackTrace")
    private suspend fun addRagGroupMessage(messages: List<ChatMessage>) {
        val firstMessage = messages.firstOrNull() ?: return

        try {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Suppress("TooGenericExceptionCaught", "PrintStackTrace")
    private suspend fun addRagMessage(message: ChatMessage) {
        try {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
