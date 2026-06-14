package com.github.mobdev778.aiadventchallenge.domain.contextmanagement.tree

import com.github.mobdev778.aiadventchallenge.domain.chat.model.ChatMessage

class TreeBuilder {

    private var messages: List<ChatMessage> = emptyList()

    fun messages(messages: List<ChatMessage>): TreeBuilder = apply {
        this.messages = messages
    }

    fun build(): TreeNode? {
        val idNodeMap = messages.map { TreeNode(it) }.associateBy { it.value.id }

        var root: TreeNode? = null

        for (message in messages) {
            val node = idNodeMap[message.id]
            if (message.parentId != null) {
                val parentNode = idNodeMap[message.parentId]!!

                val left = parentNode.left

                if (left == null) {
                    parentNode.left = node
                } else {
                    if (left.value.time < message.time) {
                        parentNode.right = node
                    } else {
                        parentNode.right = left
                        parentNode.left = node
                    }
                }
            } else {
                root = node
            }
        }

        return root
    }
}