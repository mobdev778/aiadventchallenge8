package com.github.mobdev778.aiadventchallenge.presentation.rag.ragconfigscreen

/**
 * Запечатанный интерфейс команд для экрана конфигурации RAG.
 * Определяет возможные действия пользователя на этом экране.
 * Используется в архитектуре для обмена сообщениями между слоем представления и бизнес-логикой.
 */
sealed interface RagConfigScreenCommand {
    /**
     * Команда "Назад", инициируемая пользователем для возврата с экрана конфигурации RAG.
     */
    data object Back : RagConfigScreenCommand
}
