package com.github.mobdev778.aiadventchallenge.presentation.rag.viewragdocumentscreen.model

import com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.model.RagDocumentListItem

/**
 * Состояние экрана просмотра конкретного RAG-документа.
 * Хранит текущий выбранный документ, поисковый запрос, индикатор загрузки
 * и результаты семантического поиска по фрагментам документа.
 *
 * @property document Текущий документ, отображаемый на экране. Может быть null, если документ ещё не загружен.
 * @property query Поисковый запрос, введённый пользователем для поиска по фрагментам документа.
 * @property isSearching Флаг, указывающий на то, что в данный момент выполняется поиск по фрагментам.
 * @property results Список результатов поиска, каждый из которых представляет фрагмент документа, соответствующий запросу.
 */
data class ViewRagDocumentScreenState(
    val document: RagDocumentListItem? = null,
    val query: String = "",
    val isSearching: Boolean = false,
    val results: List<ViewRagDocumentSearchResult> = emptyList(),
)

/**
 * Модель результата семантического поиска по фрагментам RAG-документа.
 * Содержит информацию об источнике, порядковом номере фрагмента и его текстовом содержимом.
 *
 * @property source Исходный источник документа, из которого взят данный фрагмент.
 * @property section Порядковый номер фрагмента (секции) внутри документа.
 * @property text Текстовое содержимое фрагмента, релевантное поисковому запросу.
 */
data class ViewRagDocumentSearchResult(
    val source: String,
    val section: Int,
    val text: String,
)
