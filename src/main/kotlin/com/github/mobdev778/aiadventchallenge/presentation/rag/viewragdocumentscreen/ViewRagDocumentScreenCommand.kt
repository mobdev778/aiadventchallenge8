package com.github.mobdev778.aiadventchallenge.presentation.rag.viewragdocumentscreen

/**
 * Запечатанный интерфейс, представляющий команды, доступные на экране просмотра RAG-документа.
 * Используется в рамках архитектурного паттерна для передачи действий от View к ViewModel.
 */
sealed interface ViewRagDocumentScreenCommand {
    /**
     * Команда для возврата на предыдущий экран или закрытия текущего экрана.
     */
    data object Back : ViewRagDocumentScreenCommand
}
