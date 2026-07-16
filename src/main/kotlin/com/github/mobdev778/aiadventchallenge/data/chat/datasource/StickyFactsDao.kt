package com.github.mobdev778.aiadventchallenge.data.chat.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.mobdev778.aiadventchallenge.data.chat.datasource.model.StickyFactsEntity
import java.util.UUID

/**
 * DAO-интерфейс для работы с таблицей обработанных сообщений, содержащих "липкие" факты.
 * Позволяет проверять, было ли сообщение уже обработано, сохранять новые записи и удалять
 * данные для конкретного чата. Использует сущность [StickyFactsEntity] для операций вставки.
 */
@Dao
interface StickyFactsDao {

    /**
     * Проверяет наличие записи об обработанном сообщении в указанном чате.
     *
     * @param chatId    идентификатор чата
     * @param messageId идентификатор сообщения
     * @return `true`, если сообщение уже было обработано ранее
     */
    @Query(
        "SELECT EXISTS(SELECT 1 FROM sticky_facts_processed_messages " +
            "WHERE chat_id = :chatId AND message_id = :messageId)",
    )
    suspend fun containsMessage(chatId: UUID, messageId: UUID): Boolean

    /**
     * Вставляет запись о новом обработанном сообщении. Если конфликт первичного ключа уже существует,
     * операция игнорируется без выбрасывания исключения.
     *
     * @param entity сущность [StickyFactsEntity], содержащая данные об обработанном сообщении
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: StickyFactsEntity)

    /**
     * Удаляет все записи об обработанных сообщениях для указанного чата.
     *
     * @param chatId идентификатор чата, для которого требуется очистка
     */
    @Query("DELETE FROM sticky_facts_processed_messages WHERE chat_id = :chatId")
    suspend fun clear(chatId: UUID)
}
