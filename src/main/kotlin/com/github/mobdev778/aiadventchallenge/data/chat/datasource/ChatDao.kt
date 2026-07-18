package com.github.mobdev778.aiadventchallenge.data.chat.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.github.mobdev778.aiadventchallenge.data.chat.datasource.model.ChatEntity
import com.github.mobdev778.aiadventchallenge.data.chat.datasource.model.ChatMessageEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Data Access Object (DAO) для работы с сущностями чатов и сообщений.
 *
 * Предоставляет асинхронные методы для чтения (в виде [Flow]) и записи (suspend-функции)
 * данных о [чатах][ChatEntity] и [сообщениях][ChatMessageEntity] в базе данных Room.
 * Поддерживает транзакционную вставку нескольких сообщений и атомарные операции удаления.
 *
 * @see ChatEntity
 * @see ChatMessageEntity
 */
@Suppress("TooManyFunctions")
@Dao
interface ChatDao {

    // region chats

    /**
     * Возвращает [Flow], эмитирующий список всех чатов, отсортированных по убыванию времени создания.
     *
     * @return [Flow], испускающий полный список [ChatEntity] при каждом изменении таблицы.
     */
    @Query("SELECT * FROM chats ORDER BY created_at_millis DESC")
    fun observeChats(): Flow<List<ChatEntity>>

    /**
     * Возвращает [Flow], эмитирующий данные одного чата по его идентификатору.
     *
     * @param chatId Уникальный идентификатор чата.
     * @return [Flow], испускающий [ChatEntity] или `null`, если чат не найден.
     */
    @Query("SELECT * FROM chats WHERE id = :chatId LIMIT 1")
    fun observeChat(chatId: UUID): Flow<ChatEntity?>

    /**
     * Вставляет или заменяет запись чата в базе данных.
     *
     * @param entity Экземпляр [ChatEntity] для вставки.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(entity: ChatEntity)

    /**
     * Удаляет чат из базы данных по его идентификатору.
     *
     * @param chatId Идентификатор удаляемого чата.
     */
    @Query("DELETE FROM chats WHERE id = :chatId")
    suspend fun deleteChatById(chatId: UUID)

    // endregion

    // region messages

    /**
     * Возвращает [Flow], эмитирующий все сообщения конкретного чата,
     * отсортированные по времени создания (по возрастанию).
     *
     * Используется для полного дерева сообщений чата (включая все ветки).
     *
     * @param chatId Идентификатор чата, чьи сообщения необходимо наблюдать.
     * @return [Flow], испускающий список [ChatMessageEntity] при каждом изменении.
     */
    @Query("SELECT * FROM chat_messages WHERE chat_id = :chatId ORDER BY created_at_millis ASC")
    fun observeMessages(chatId: UUID): Flow<List<ChatMessageEntity>>

    /**
     * Возвращает [Flow], эмитирующий только корневые сообщения чата (те, у которых отсутствует родительское сообщение).
     *
     * Удобно для отображения первого уровня диалога без дочерних ветвей.
     *
     * @param chatId Идентификатор чата.
     * @return [Flow], испускающий список корневых [ChatMessageEntity].
     */
    @Query(
        "SELECT * FROM chat_messages " +
            "WHERE chat_id = :chatId AND parent_id IS NULL " +
            "ORDER BY created_at_millis ASC",
    )
    fun observeRootMessages(chatId: UUID): Flow<List<ChatMessageEntity>>

    /**
     * Вставляет или заменяет одно сообщение в базе данных.
     *
     * @param entity Экземпляр [ChatMessageEntity] для вставки.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(entity: ChatMessageEntity)

    /**
     * Транзакционная вставка списка сообщений.
     *
     * Все сообщения вставляются в рамках одной транзакции, гарантируя атомарность операции.
     * Каждое сообщение вставляется с использованием [insertMessage].
     *
     * @param entities Список [ChatMessageEntity] для вставки.
     */
    @Transaction
    suspend fun insertMessagesWithTransaction(entities: List<ChatMessageEntity>) {
        entities.forEach { entity ->
            insertMessage(entity)
        }
    }

    /**
     * Удаляет конкретное сообщение по его идентификатору.
     *
     * @param messageId Идентификатор удаляемого сообщения.
     */
    @Query("DELETE FROM chat_messages WHERE id = :messageId")
    suspend fun deleteMessageById(messageId: UUID)

    /**
     * Удаляет все сообщения в указанном чате (очистка чата).
     *
     * @param chatId Идентификатор чата, сообщения которого удаляются.
     */
    @Query("DELETE FROM chat_messages WHERE chat_id = :chatId")
    suspend fun clearMessages(chatId: UUID)

    /**
     * Удаляет все сообщения, принадлежащие данному чату.
     *
     * Синоним [clearMessages] для совместимости; удаляет все сообщения с заданным `chatId`.
     *
     * @param chatId Идентификатор чата.
     */
    @Query("DELETE FROM chat_messages WHERE chat_id = :chatId")
    suspend fun deleteMessagesByChatId(chatId: UUID)

    // endregion
}
