package com.github.mobdev778.aiadventchallenge.domain.contextmanagement.tree

import com.github.mobdev778.aiadventchallenge.domain.chat.model.ChatMessage

/**
 * Построитель бинарного дерева диалога из плоского списка сообщений [ChatMessage].
 *
 * Организует сообщения в иерархическую структуру [TreeNode], связывая каждое сообщение
 * с родителем по полю [ChatMessage.parentId] и распределяя дочерние узлы по левому
 * и правому поддеревьям в хронологическом порядке. Левое поддерево содержит более
 * раннее сообщение, а правое — более позднее (по [ChatMessage.time]), что позволяет
 * корректно восстанавливать древо диалога даже при наличии альтернативных веток.
 *
 * Использование:
 * ```kotlin
 * val root = TreeBuilder()
 *     .messages(chatMessages)
 *     .build()
 * ```
 */
class TreeBuilder {

    /**
     * Исходный список сообщений, из которых будет построено дерево.
     */
    private var messages: List<ChatMessage> = emptyList()

    /**
     * Устанавливает список сообщений для последующего построения дерева.
     *
     * @param messages Список всех сообщений чата, которые необходимо преобразовать
     *                 в древовидную структуру. Не должен быть пустым при вызове [build].
     * @return Текущий экземпляр [TreeBuilder] для цепочечных вызовов.
     */
    fun messages(messages: List<ChatMessage>): TreeBuilder = apply {
        this.messages = messages
    }

    /**
     * Выполняет построение бинарного дерева диалога на основе заранее переданного
     * списка сообщений.
     *
     * Алгоритм:
     * 1. Создаёт узлы [TreeNode] для каждого сообщения и строит отображение
     *    `id → узел` для быстрого поиска.
     * 2. Для каждого сообщения находит родительский узел по [ChatMessage.parentId].
     * 3. Если родитель существует, вставляет текущий узел в левое или правое поддерево:
     *    - при пустом левом поддереве — в левое;
     *    - иначе сравнивает времена и сохраняет хронологический порядок (левое — более
     *      раннее сообщение).
     * 4. Если родитель отсутствует, сообщение считается корневым и сохраняется в `root`.
     *
     * @return Корневой узел [TreeNode] построенного дерева, либо `null`, если список
     *         сообщений пуст или не содержит корневого элемента.
     */
    fun build(): TreeNode? {
        val idNodeMap = messages.map { TreeNode(it) }.associateBy { it.value.id }

        var root: TreeNode? = null

        for (message in messages) {
            val node = idNodeMap[message.id]
            val parentNode = if (message.parentId == null) null else idNodeMap[message.parentId]

            if (parentNode != null) {
                insertChildNode(parentNode, node!!)
            } else {
                root = node
            }
        }

        return root
    }

    private fun insertChildNode(parent: TreeNode, child: TreeNode) {
        val left = parent.left
        if (left == null) {
            parent.left = child
        } else {
            if (left.value.time < child.value.time) {
                parent.right = child
            } else {
                parent.right = left
                parent.left = child
            }
        }
    }
}
