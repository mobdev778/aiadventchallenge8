package com.github.mobdev778.aiadventchallenge.domain.contextmanagement.tree

import com.github.mobdev778.aiadventchallenge.domain.chat.model.ChatMessage

class TreeNode(val value: ChatMessage) {
    var left: TreeNode? = null
    var right: TreeNode? = null
}