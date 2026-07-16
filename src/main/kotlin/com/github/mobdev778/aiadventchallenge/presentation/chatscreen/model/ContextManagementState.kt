package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model

/**
 * Состояние управления контекстом сообщений в чате.
 * Определяет стратегию, которая используется для ограничения или организации
 * предыстории диалога при генерации ответов моделью.
 */
sealed interface ContextManagementState {
    /**
     * Отсутствие специального управления контекстом.
     * Используется вся доступная история сообщений.
     */
    data object None : ContextManagementState

    /**
     * Стратегия скользящего окна: модель видит только последние N сообщений.
     * @param messages текущее количество сообщений в окне.
     * @param maxMessages максимальный размер окна.
     */
    data class SlidingWindow(val messages: Int, val maxMessages: Int) : ContextManagementState

    /**
     * Стратегия "закреплённых фактов": часть сообщений считается фактами и сохраняется,
     * остальное — скользящее окно.
     * @param messages текущее количество сообщений в окне.
     * @param maxMessages максимальный размер окна (без учёта фактов).
     */
    data class StickFacts(val messages: Int, val maxMessages: Int) : ContextManagementState

    /**
     * Стратегия ветвления: контекст разделяется на независимые ветки диалога.
     */
    data object Branching : ContextManagementState
}
