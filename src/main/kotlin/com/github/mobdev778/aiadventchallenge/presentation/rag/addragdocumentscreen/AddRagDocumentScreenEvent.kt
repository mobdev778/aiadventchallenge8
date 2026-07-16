package com.github.mobdev778.aiadventchallenge.presentation.rag.addragdocumentscreen

/**
 * Запечатанный интерфейс, представляющий все возможные события,
 * которые могут быть отправлены с экрана добавления RAG-документа.
 * Используется для централизованной обработки пользовательских действий
 * в соответствии с паттерном UDF (Unidirectional Data Flow).
 */
sealed interface AddRagDocumentScreenEvent {

    /**
     * Событие нажатия на кнопку возврата назад.
     * Инициирует закрытие текущего экрана без сохранения изменений.
     */
    data object OnBackClick : AddRagDocumentScreenEvent

    /**
     * Событие нажатия на кнопку выбора источника документа.
     * Открывает диалог или навигацию для выбора файла/ресурса,
     * который будет использован в качестве основы RAG-документа.
     */
    data object OnChooseSourceClick : AddRagDocumentScreenEvent

    /**
     * Событие изменения выбранного источника документа.
     *
     * @param value Новый путь или идентификатор выбранного источника.
     */
    data class OnSourceChange(val value: String) : AddRagDocumentScreenEvent

    /**
     * Событие изменения названия документа.
     *
     * @param value Новое название RAG-документа, введённое пользователем.
     */
    data class OnTitleChange(val value: String) : AddRagDocumentScreenEvent

    /**
     * Событие изменения стратегии разбиения текста на чанки (chunking).
     *
     * @param index Индекс выбранной стратегии чанкинга в списке доступных вариантов.
     */
    data class OnChunkingStrategyChange(val index: Int) : AddRagDocumentScreenEvent

    /**
     * Событие нажатия на кнопку добавления документа.
     * Инициирует процесс сохранения нового RAG-документа с заданными параметрами.
     */
    data object OnAddClick : AddRagDocumentScreenEvent
}
