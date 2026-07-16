package com.github.mobdev778.aiadventchallenge.presentation.rag.addingragdocumentscreen

/**
 * Запечатанный интерфейс, представляющий множество команд,
 * доступных для экрана добавления RAG-документа.
 *
 * Используется в архитектуре presentation-слоя (MVVM/MVI)
 * для однозначной передачи навигационных событий и пользовательских
 * действий от ViewModel к UI-компонентам.
 */
sealed interface AddingRagDocumentScreenCommand {
    /**
     * Команда возврата к экрану добавления документа.
     *
     * Инициирует навигацию назад к форме или интерфейсу добавления
     * нового RAG-документа без завершения общего потока.
     */
    data object BackToAddDocument : AddingRagDocumentScreenCommand

    /**
     * Команда возврата к списку документов.
     *
     * Инициирует навигацию назад к экрану со списком всех
     * ранее загруженных RAG-документов.
     */
    data object BackToDocumentList : AddingRagDocumentScreenCommand
}
