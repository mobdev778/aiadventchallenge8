package com.github.mobdev778.aiadventchallenge.domain.chat

import com.github.mobdev778.aiadventchallenge.data.chat.repository.StickyFactsRepository
import com.github.mobdev778.aiadventchallenge.data.rag.repository.RagChatRepository
import com.github.mobdev778.aiadventchallenge.data.settings.repository.SettingsRepository
import com.github.mobdev778.aiadventchallenge.domain.chat.model.ChatMessage
import com.github.mobdev778.aiadventchallenge.domain.contextmanagement.NoStrategy
import com.github.mobdev778.aiadventchallenge.domain.contextmanagement.slidingwindow.SlidingWindowStrategy
import com.github.mobdev778.aiadventchallenge.domain.contextmanagement.ContextManagementStrategy
import com.github.mobdev778.aiadventchallenge.domain.contextmanagement.branching.BranchingStrategy
import com.github.mobdev778.aiadventchallenge.domain.contextmanagement.stickyFacts.ConvertMapToTextUseCase
import com.github.mobdev778.aiadventchallenge.domain.contextmanagement.stickyFacts.ConvertTextToMapUseCase
import com.github.mobdev778.aiadventchallenge.domain.contextmanagement.stickyFacts.GetStickyFactsUseCase
import com.github.mobdev778.aiadventchallenge.domain.contextmanagement.stickyFacts.StickyFactsStrategy
import com.github.mobdev778.aiadventchallenge.domain.settings.model.ContextManagementType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single
import java.util.UUID

/**
 * Возвращает список сообщений, которые укладываются в заданный лимит
 */
@Single
class ObserveWindowMessagesUseCase(
    private val chatRepository: RagChatRepository,
    private val settingsRepository: SettingsRepository,
    private val getStickyFactsUseCase: GetStickyFactsUseCase,
    private val convertTextToMapUseCase: ConvertTextToMapUseCase,
    private val convertMapToTextUseCase: ConvertMapToTextUseCase,
    private val stickyFactsRepository: StickyFactsRepository,
) {

    fun invoke(chatId: UUID): Flow<List<ChatMessage>> {
        return combine(
            chatRepository.observeMessages(chatId).distinctUntilChanged(),
            settingsRepository.observeSettings()
                .map {
                    StrategyState(
                        it.contextManagementType,
                        it.maxMessages,
                        it.maxTokens,
                        it.recursiveSummationMaxMessages,
                        it.stickyFactsMaxMessages,
                    )
                }
                .distinctUntilChanged(),
        ) { messages: List<ChatMessage>, state: StrategyState ->
            val strategy: ContextManagementStrategy = getContextManagementStrategy(state)
            strategy.selectMessages(chatId, messages)
        }
    }

    private fun getContextManagementStrategy(
        state: StrategyState,
    ): ContextManagementStrategy {
        val type = state.contextManagementType
        return when (type) {
            ContextManagementType.None -> {
                NoStrategy()
            }
            ContextManagementType.SlidingWindow -> {
                SlidingWindowStrategy(state.maxMessages)
            }
            ContextManagementType.StickyFacts -> {
                StickyFactsStrategy(
                    getStickyFactsUseCase = getStickyFactsUseCase,
                    convertTextToMapUseCase = convertTextToMapUseCase,
                    convertMapToTextUseCase = convertMapToTextUseCase,
                    maxMessages = state.stickyFactsMaxMessages,
                    stickyFactsRepository = stickyFactsRepository,
                    chatRepository = chatRepository,
                )
            }

            ContextManagementType.Branching -> {
                BranchingStrategy()
            }
        }
    }

    private data class StrategyState(
        val contextManagementType: ContextManagementType,
        val maxMessages: Int,
        val maxTokens: Int,
        val recursiveSummationMaxMessages: Int,
        val stickyFactsMaxMessages: Int,
    )
}
