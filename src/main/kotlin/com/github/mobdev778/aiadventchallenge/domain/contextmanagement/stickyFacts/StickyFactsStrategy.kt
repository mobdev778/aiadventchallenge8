package com.github.mobdev778.aiadventchallenge.domain.contextmanagement.stickyFacts

import com.github.mobdev778.aiadventchallenge.data.chat.repository.ChatRepository
import com.github.mobdev778.aiadventchallenge.data.chat.repository.StickyFactsRepository
import com.github.mobdev778.aiadventchallenge.domain.chat.model.ChatMessage
import com.github.mobdev778.aiadventchallenge.domain.chat.model.MessageType
import com.github.mobdev778.aiadventchallenge.domain.contextmanagement.ContextManagementStrategy
import com.github.mobdev778.aiadventchallenge.domain.contextmanagement.tree.TreeBuilder
import com.github.mobdev778.aiadventchallenge.domain.contextmanagement.tree.TreeNode
import java.util.UUID

class StickyFactsStrategy(
    private val getStickyFactsUseCase: GetStickyFactsUseCase,
    private val convertTextToMapUseCase: ConvertTextToMapUseCase,
    private val convertMapToTextUseCase: ConvertMapToTextUseCase,
    private val maxMessages: Int,
    private val stickyFactsRepository: StickyFactsRepository,
    private val chatRepository: ChatRepository,
) : ContextManagementStrategy {

    override suspend fun selectMessages(chatId: UUID, history: List<ChatMessage>): List<ChatMessage> {
        var stickyMessage = history.firstOrNull { it.type == MessageType.StickyFacts } ?: ChatMessage(
            id = UUID.randomUUID(),
            chatId = chatId,
            type = MessageType.StickyFacts,
            parentId = null,
            branchB = false,
            text = " - нет сообщений -",
            time = 0, // наше сообщение всегда должно быть самым первым в списке
            tokens = 0,
            rank = 0,
        )

        val root = TreeBuilder().messages(history).build()

        val result = ArrayList<ChatMessage>()
        var node: TreeNode? = root
        while (node != null) {
            result.add(node.value)
            node = node.left
        }

        val nonStickyMessages = result.filter {
            it.type != MessageType.StickyFacts && !stickyFactsRepository.containsMessage(chatId, it.id)
        }

        if (nonStickyMessages.isNotEmpty()) {
            val oldStickyMessageMap = convertTextToMapUseCase.invoke(stickyMessage.text).toMutableMap()

            val stickyFacts = getStickyFactsUseCase.invoke(nonStickyMessages)
            val stickyFactsMap = convertTextToMapUseCase.invoke(stickyFacts?.text ?: "")
            val joinedStickyFacts = convertMapToTextUseCase.invoke(oldStickyMessageMap + stickyFactsMap)
            stickyMessage = stickyMessage.copy(
                time = 0,
                text = joinedStickyFacts,
            )
            // помечаем сначала сообщения как сообщения для игнорирования, чтобы не запускать цикл заново
            nonStickyMessages.forEach {
                stickyFactsRepository.addMessage(chatId, it.id)
            }
            // обновляем репозиторий
            chatRepository.add(stickyMessage)
        }

        return listOf(stickyMessage) + result.filter { it.type != MessageType.StickyFacts }.takeLast(maxMessages)
    }

    override suspend fun clear(chatId: UUID) {
        stickyFactsRepository.clear(chatId)
    }
}
