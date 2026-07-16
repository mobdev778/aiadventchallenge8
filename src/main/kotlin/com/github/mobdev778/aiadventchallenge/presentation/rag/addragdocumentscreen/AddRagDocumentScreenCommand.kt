package com.github.mobdev778.aiadventchallenge.presentation.rag.addragdocumentscreen

import com.github.mobdev778.aiadventchallenge.presentation.rag.addragdocumentscreen.model.AddRagDocumentScreenState.ChunkingStrategy

/**
 * Команды, обрабатываемые экраном добавления документа в RAG.
 *
 * Представляют намерения пользователя или системы, которые должен выполнить
 * презентер/ViewModel экрана [AddRagDocumentScreen]. Используется в паттерне
 * однонаправленного потока данных.
 */
sealed interface AddRagDocumentScreenCommand {

    /**
     * Команда возврата с экрана (навигация назад).
     */
    data object Back : AddRagDocumentScreenCommand

    /**
     * Команда открытия диалога или перехода к экрану добавления документа
     * с предзаполненными данными.
     *
     * @param source Источник документа (URL, путь к файлу и т.п.).
     * @param title Заголовок документа.
     * @param chunkingStrategy Стратегия разбиения текста на чанки.
     */
    data class OpenAddingDocument(
        val source: String,
        val title: String,
        val chunkingStrategy: ChunkingStrategy,
    ) : AddRagDocumentScreenCommand
}
