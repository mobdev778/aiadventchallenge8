package com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen

import com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.model.RagDocumentListItem

/**
 * Запечатанный интерфейс, описывающий все возможные команды (пользовательские действия)
 * на экране списка RAG-документов.
 *
 * Используется для передачи намерений от UI-слоя к ViewModel или другому обработчику.
 * Каждая команда представляет собой конкретное действие, инициированное пользователем.
 */
sealed interface RagDocumentListScreenCommand {
    /**
     * Команда возврата на предыдущий экран.
     */
    data object Back : RagDocumentListScreenCommand

    /**
     * Команда открытия экрана добавления нового документа.
     */
    data object OpenAddDocument : RagDocumentListScreenCommand

    /**
     * Команда открытия экрана конфигурации RAG.
     */
    data object OpenRagConfig : RagDocumentListScreenCommand

    /**
     * Команда открытия экрана детального просмотра выбранного документа.
     *
     * @param document Элемент списка документов, содержащий информацию о выбранном документе.
     */
    data class OpenDocument(val document: RagDocumentListItem) : RagDocumentListScreenCommand
}
