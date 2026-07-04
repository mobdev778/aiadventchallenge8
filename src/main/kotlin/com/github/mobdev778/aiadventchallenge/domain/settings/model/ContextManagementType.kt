package com.github.mobdev778.aiadventchallenge.domain.settings.model

/**
 * Управление контекстом.
 */
enum class ContextManagementType {
    None,
    SlidingWindow, // обычное скользящее окно
    StickyFacts,   // (key-value память)
    Branching,     // ветки диалога
}
