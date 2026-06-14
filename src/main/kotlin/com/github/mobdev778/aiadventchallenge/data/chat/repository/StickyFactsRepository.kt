package com.github.mobdev778.aiadventchallenge.data.chat.repository

import com.github.mobdev778.aiadventchallenge.data.chat.datasource.StickyFactsDao
import com.github.mobdev778.aiadventchallenge.data.chat.datasource.model.StickyFactsEntity
import org.koin.core.annotation.Single
import java.util.UUID

@Single
class StickyFactsRepository(
    private val stickyFactsDao: StickyFactsDao,
) {
    suspend fun containsMessage(chatId: UUID, messageId: UUID): Boolean {
        return stickyFactsDao.containsMessage(chatId = chatId, messageId = messageId)
    }

    suspend fun addMessage(chatId: UUID, messageId: UUID) {
        stickyFactsDao.insert(
            StickyFactsEntity(
                chatId = chatId,
                messageId = messageId,
            ),
        )
    }

    suspend fun clear(chatId: UUID) {
        stickyFactsDao.clear(chatId)
    }
}
