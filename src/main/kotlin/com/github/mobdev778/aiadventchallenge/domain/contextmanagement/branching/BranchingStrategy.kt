package com.github.mobdev778.aiadventchallenge.domain.contextmanagement.branching

import com.github.mobdev778.aiadventchallenge.domain.chat.model.ChatMessage
import com.github.mobdev778.aiadventchallenge.domain.contextmanagement.ContextManagementStrategy
import com.github.mobdev778.aiadventchallenge.domain.contextmanagement.tree.TreeBuilder
import com.github.mobdev778.aiadventchallenge.domain.contextmanagement.tree.TreeNode
import java.util.UUID

/**
 * Реализация стратегии управления контекстом, которая выделяет активную ветку
 * диалога на основе флагов ветвления [ChatMessage.branchB].
 *
 * Стратегия преобразует историю сообщений в бинарное дерево с помощью [TreeBuilder],
 * после чего обходит его, выбирая в каждом узле левое или правое поддерево в
 * зависимости от значения поля [ChatMessage.branchB]. Таким образом формируется
 * линейная последовательность сообщений, соответствующая текущему активному пути
 * в древе диалога.
 *
 * @see ContextManagementStrategy
 * @see TreeBuilder
 * @see TreeNode
 */
class BranchingStrategy : ContextManagementStrategy {

    /**
     * Выбирает из полной истории сообщений чата те, которые принадлежат активной
     * ветке диалога.
     *
     * Алгоритм:
     * - Строит [TreeNode] из переданных сообщений, связывая их через parentId.
     * - Начиная с корневого узла, добавляет сообщение в результат и переходит к
     *   левому (branchB = false) или правому (branchB = true) дочернему узлу.
     * - Продолжает до достижения листа дерева.
     *
     * @param chatId Идентификатор чата, для которого выполняется выборка.
     * @param history Полный список сообщений чата.
     * @return Список сообщений, образующих активную ветку диалога.
     */
    override suspend fun selectMessages(
        chatId: UUID,
        history: List<ChatMessage>
    ): List<ChatMessage> {
        val root = TreeBuilder().messages(history).build()

        val result = ArrayList<ChatMessage>()
        var node: TreeNode? = root
        while (node != null) {
            result.add(node.value)
            if (node.value.branchB) {
                node = node.right
            } else {
                node = node.left
            }
        }

        return result
    }

    /**
     * Очищает данные, связанные с указанным чатом. В данной реализации
     * не выполняет никаких действий, так как стратегия не сохраняет состояние.
     *
     * @param chatId Идентификатор чата.
     */
    override suspend fun clear(chatId: UUID) {
        // No op
    }
}
