package com.github.mobdev778.aiadventchallenge.data.chat.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.mobdev778.aiadventchallenge.data.chat.datasource.model.StickyFactsEntity
import java.util.UUID

@Dao
interface StickyFactsDao {

    @Query(
        "SELECT EXISTS(SELECT 1 FROM sticky_facts_processed_messages " +
            "WHERE chat_id = :chatId AND message_id = :messageId)",
    )
    suspend fun containsMessage(chatId: UUID, messageId: UUID): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: StickyFactsEntity)

    @Query("DELETE FROM sticky_facts_processed_messages WHERE chat_id = :chatId")
    suspend fun clear(chatId: UUID)
}
