package com.github.mobdev778.aiadventchallenge.domain.contextmanagement.slidingwindow

import com.github.mobdev778.aiadventchallenge.domain.chat.model.ChatMessage
import com.github.mobdev778.aiadventchallenge.domain.contextmanagement.ContextManagementStrategy
import com.github.mobdev778.aiadventchallenge.domain.contextmanagement.tree.TreeBuilder
import com.github.mobdev778.aiadventchallenge.domain.contextmanagement.tree.TreeNode
import java.util.UUID

class SlidingWindowStrategy(val maxMessages: Int) : ContextManagementStrategy {

    override suspend fun selectMessages(chatId: UUID, history: List<ChatMessage>): List<ChatMessage> {
        val root = TreeBuilder().messages(history).build()

        val result = ArrayList<ChatMessage>()
        var node: TreeNode? = root
        while (node != null) {
            result.add(node.value)
            node = node.left
        }

        return result.takeLast(maxMessages)
    }

    override suspend fun clear(chatId: UUID) {
        // No op
    }
}