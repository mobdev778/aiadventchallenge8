package com.github.mobdev778.aiadventchallenge.presentation.rag.addingragdocumentscreen

/**
 * Запечатанный интерфейс, представляющий все возможные события, которые могут произойти
 * на экране добавления документов RAG.
 * Используется для унификации обработки пользовательских действий и навигации.
 */
sealed interface AddingRagDocumentScreenEvent {
    /**
     * Событие, возникающее при нажатии пользователем кнопки прерывания операции.
     * Может инициировать закрытие экрана или отмену текущей задачи.
     */
    data object OnAbortClick : AddingRagDocumentScreenEvent
}
