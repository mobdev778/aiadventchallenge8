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

@Suppress("TooManyFunctions")
@Dao
interface ChatDao {

    // region chats

    @Query("SELECT * FROM chats ORDER BY created_at_millis DESC")
    fun observeChats(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chats WHERE id = :chatId LIMIT 1")
    fun observeChat(chatId: UUID): Flow<ChatEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(entity: ChatEntity)

    @Query("DELETE FROM chats WHERE id = :chatId")
    suspend fun deleteChatById(chatId: UUID)

    // endregion

    // region messages

    @Query("SELECT * FROM chat_messages WHERE chat_id = :chatId ORDER BY created_at_millis ASC")
    fun observeMessages(chatId: UUID): Flow<List<ChatMessageEntity>>

    @Query(
        "SELECT * FROM chat_messages " +
            "WHERE chat_id = :chatId AND parent_id IS NULL " +
            "ORDER BY created_at_millis ASC",
    )
    fun observeRootMessages(chatId: UUID): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(entity: ChatMessageEntity)

    @Transaction
    suspend fun insertMessagesWithTransaction(entities: List<ChatMessageEntity>) {
        entities.forEach { entity ->
            insertMessage(entity)
        }
    }

    @Query("DELETE FROM chat_messages WHERE id = :messageId")
    suspend fun deleteMessageById(messageId: UUID)

    @Query("DELETE FROM chat_messages WHERE chat_id = :chatId")
    suspend fun clearMessages(chatId: UUID)

    @Query("DELETE FROM chat_messages WHERE chat_id = :chatId")
    suspend fun deleteMessagesByChatId(chatId: UUID)

    // endregion
}
