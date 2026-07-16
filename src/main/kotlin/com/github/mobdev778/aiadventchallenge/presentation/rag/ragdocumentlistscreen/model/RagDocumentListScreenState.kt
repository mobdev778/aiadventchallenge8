package com.github.mobdev778.aiadventchallenge.presentation.rag.ragdocumentlistscreen.model

import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagConfig

/**
 * Состояние экрана списка документов RAG-системы.
 *
 * Неизменяемый контейнер, объединяющий текущую конфигурацию модуля RAG и список элементов документов,
 * предназначенных для отображения на экране. Используется в качестве источника данных для UI-компонентов,
 * работающих с документами в контексте Retrieval-Augmented Generation.
 *
 * @property ragConfig Конфигурация модуля RAG, определяющая параметры фильтрации и предобработки документов.
 *                    Включает настройки первичного поиска (top-K), тип фильтрации, флаг переформулировки запроса,
 *                    пороги сходства и пути к моделям (подробнее см. [RagConfig]).
 * @property documents Список элементов документов, каждый из которых содержит ключевую информацию:
 *                    уникальный идентификатор, источник, заголовок и количество фрагментов (чанков),
 *                    на которые разбит документ для семантического поиска (см. [RagDocumentListItem]).
 */
data class RagDocumentListScreenState(
    val ragConfig: RagConfig,
    val documents: List<RagDocumentListItem>,
)
