package com.github.mobdev778.aiadventchallenge.domain.chathistory

import com.github.mobdev778.aiadventchallenge.data.chathistory.repository.ChatHistoryRepository
import com.github.mobdev778.aiadventchallenge.data.settings.repository.SettingsRepository
import com.github.mobdev778.aiadventchallenge.domain.chathistory.model.ChatMessage
import com.github.mobdev778.aiadventchallenge.domain.messageselection.FullHistoryStrategy
import com.github.mobdev778.aiadventchallenge.domain.messageselection.MessageLimitStrategy
import com.github.mobdev778.aiadventchallenge.domain.messageselection.MessageSelectionStrategy
import com.github.mobdev778.aiadventchallenge.domain.messageselection.MessageSelectionType
import com.github.mobdev778.aiadventchallenge.domain.messageselection.TokenLimitStrategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

/**
 * Возвращает список сообщений, которые укладываются в заданный лимит
 */
@Single
class ObserveWindowMessagesUseCase(
    private val chatHistoryRepository: ChatHistoryRepository,
    private val settingsRepository: SettingsRepository,
) {

    fun invoke(): Flow<List<ChatMessage>> {
        return combine(
            chatHistoryRepository.observe(),
            settingsRepository.observeSettings()
                .map { StrategyState(it.messageSelectionType, it.maxMessages, it.maxTokens) }
                .distinctUntilChanged(),
        ) { messages: List<ChatMessage>, state: StrategyState ->
            val strategy: MessageSelectionStrategy = getMessageSelectionStrategy(state)
            strategy.selectMessages(messages)
        }
    }

    private fun getMessageSelectionStrategy(
        state: StrategyState,
    ): MessageSelectionStrategy {
        val type = state.messageSelectionType
        return when (type) {
            MessageSelectionType.FullHistory -> {
                FullHistoryStrategy()
            }

            MessageSelectionType.MessageLimit -> {
                MessageLimitStrategy(state.maxMessages)
            }

            MessageSelectionType.TokenLimit -> {
                TokenLimitStrategy(state.maxTokens)
            }
        }
    }

    private data class StrategyState(
        val messageSelectionType: MessageSelectionType,
        val maxMessages: Int,
        val maxTokens: Int,
    )
}