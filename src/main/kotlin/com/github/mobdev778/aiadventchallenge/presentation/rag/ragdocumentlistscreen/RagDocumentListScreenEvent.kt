package com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen

import com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.model.RagDocumentListItem
import java.util.UUID

/**
 * События экрана списка RAG-документов.
 *
 * Представляет собой sealed-интерфейс, определяющий все возможные однократные
 * пользовательские действия на экране отображения документов Retrieval-Augmented
 * Generation (RAG) системы. Каждое событие инкапсулирует необходимые данные для
 * обработки конкретного действия (например, идентификатор документа или его модель).
 *
 * Используется для передачи событий от View к ViewModel в архитектуре MVVM.
 */
sealed interface RagDocumentListScreenEvent {
    /**
     * Событие нажатия на кнопку "Назад".
     *
     * Инициирует возврат к предыдущему экрану или закрытие текущего.
     */
    data object OnBackClick : RagDocumentListScreenEvent

    /**
     * Событие нажатия на кнопку добавления нового документа.
     *
     * Запускает процесс выбора и импорта документа в RAG-систему.
     */
    data object OnAddClick : RagDocumentListScreenEvent

    /**
     * Событие нажатия на элемент конфигурации RAG.
     *
     * Открывает экран настроек RAG-параметров (например, параметры индексации,
     * модели эмбеддингов и т.д.).
     */
    data object OnRagConfigClick : RagDocumentListScreenEvent

    /**
     * Событие нажатия на элемент списка документов.
     *
     * Инициирует переход к детальному просмотру или выбору действия для конкретного документа.
     *
     * @param document Модель данных элемента списка (RagDocumentListItem), содержащая
     *                 идентификатор, источник, заголовок и количество чанков документа.
     */
    data class OnDocumentClick(val document: RagDocumentListItem) : RagDocumentListScreenEvent

    /**
     * Событие запроса на удаление документа по его идентификатору.
     *
     * @param documentId Уникальный идентификатор документа в системе (UUID), который необходимо удалить.
     */
    data class OnDeleteClick(val documentId: UUID) : RagDocumentListScreenEvent
}
