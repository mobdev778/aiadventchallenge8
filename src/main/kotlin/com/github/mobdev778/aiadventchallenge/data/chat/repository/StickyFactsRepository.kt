package com.github.mobdev778.aiadventchallenge.data.chat.repository

import com.github.mobdev778.aiadventchallenge.data.chat.datasource.StickyFactsDao
import com.github.mobdev778.aiadventchallenge.data.chat.datasource.model.StickyFactsEntity
import org.koin.core.annotation.Single
import java.util.UUID

/**
 * Репозиторий для управления обработанными сообщениями с липкими фактами.
 *
 * Обеспечивает проверку факта обработки, добавление новых записей и очистку истории
 * для конкретного чата. Служит единственной точкой доступа к данным через [StickyFactsDao],
 * инкапсулируя логику создания сущностей [StickyFactsEntity].
 */
@Single
class StickyFactsRepository(
    private val stickyFactsDao: StickyFactsDao,
) {

    /**
     * Проверяет, было ли сообщение уже обработано в рамках указанного чата.
     *
     * @param chatId    идентификатор чата
     * @param messageId идентификатор сообщения
     * @return `true`, если запись о сообщении присутствует в хранилище
     */
    suspend fun containsMessage(chatId: UUID, messageId: UUID): Boolean {
        return stickyFactsDao.containsMessage(chatId = chatId, messageId = messageId)
    }

    /**
     * Фиксирует факт обработки сообщения в заданном чате.
     *
     * Создаёт сущность [StickyFactsEntity] и сохраняет её через DAO.
     * Если запись с таким идентификатором сообщения уже существует, операция игнорируется
     * (конфликт первичного ключа обрабатывается стратегией IGNORE на уровне DAO).
     *
     * @param chatId    идентификатор чата
     * @param messageId идентификатор сообщения
     */
    suspend fun addMessage(chatId: UUID, messageId: UUID) {
        stickyFactsDao.insert(
            StickyFactsEntity(
                chatId = chatId,
                messageId = messageId,
            ),
        )
    }

    /**
     * Удаляет все записи об обработанных сообщениях для указанного чата.
     *
     * @param chatId идентификатор чата, историю которого требуется очистить
     */
    suspend fun clear(chatId: UUID) {
        stickyFactsDao.clear(chatId)
    }
}
