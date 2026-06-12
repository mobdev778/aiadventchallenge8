package com.github.mobdev778.aiadventchallenge.data.chathistory.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.github.mobdev778.aiadventchallenge.data.chathistory.datasource.model.ChatMessageEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface ChatDao {

    @Query("SELECT * FROM chat_messages ORDER BY created_at_millis ASC")
    fun observeAll(): Flow<List<ChatMessageEntity>>

    @Query(
        "SELECT * FROM chat_messages " +
            "WHERE parent_id IS NULL " +
            "ORDER BY created_at_millis ASC",
    )
    fun observeRootMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ChatMessageEntity)

    @Transaction
    suspend fun insertWithTransaction(entities: List<ChatMessageEntity>) {
        entities.forEach { entity ->
            insert(entity)
        }
    }

    @Query("DELETE FROM chat_messages WHERE id = :messageId")
    suspend fun deleteById(messageId: UUID)

    @Query("DELETE FROM chat_messages")
    suspend fun clear()
}