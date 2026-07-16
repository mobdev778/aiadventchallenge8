package com.github.mobdev778.aiadventchallenge.presentation.rag.addingragdocumentscreen.model

/**
 * Состояние экрана добавления RAG-документа.
 *
 * Представляет собой неизменяемый снимок данных, необходимых для отображения
 * пользовательского интерфейса на экране добавления документа в систему
 * retrieval-augmented generation (RAG). Используется в связке с паттерном
 * однонаправленного потока данных (UDF) для отделения состояния от UI-логики.
 *
 * @property progress Текущий прогресс операции добавления документа
 *   в диапазоне от `0f` (операция не начата) до `1f` (операция завершена).
 */
data class AddingRagDocumentScreenState(
    val progress: Float = 0f,
)
