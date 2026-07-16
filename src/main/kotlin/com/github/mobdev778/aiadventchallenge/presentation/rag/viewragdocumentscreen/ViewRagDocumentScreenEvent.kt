package com.github.mobdev778.aiadventchallenge.presentation.rag.viewragdocumentscreen

/**
 * Запечатанный интерфейс, представляющий все возможные пользовательские события
 * на экране просмотра RAG-документа.
 *
 * Используется в архитектуре UI для централизованной обработки действий пользователя
 * (навигация назад, ввод поискового запроса, инициирование поиска) через единый
 * канал событий ViewModel или компонента экрана.
 */
sealed interface ViewRagDocumentScreenEvent {

    /**
     * Событие нажатия на кнопку "Назад".
     * Инициирует навигацию возврата к предыдущему экрану.
     */
    data object OnBackClick : ViewRagDocumentScreenEvent

    /**
     * Событие изменения текста в поле поискового запроса.
     *
     * @param value Текущее значение текста, введённое пользователем в строку поиска.
     */
    data class OnQueryChange(val value: String) : ViewRagDocumentScreenEvent

    /**
     * Событие нажатия на кнопку поиска.
     * Запускает процесс семантического поиска по RAG-документу на основе
     * введённого поискового запроса.
     */
    data object OnSearchClick : ViewRagDocumentScreenEvent
}
