package com.github.mobdev778.aiadventchallenge.domain.rag.filechunker

import java.io.File

/**
 * Представляет фрагмент (чанк) файла, полученный в результате разбиения исходного файла на смысловые секции.
 *
 * Используется в контексте RAG (Retrieval-Augmented Generation) для хранения и передачи
 * отдельных сегментов текста вместе с метаинформацией о файле-источнике и порядковом номере секции.
 *
 * @property file Исходный файл, из которого был извлечён данный фрагмент текста.
 * @property section Порядковый номер секции (фрагмента) в пределах исходного файла. Нумерация начинается с 0.
 * @property text Текстовое содержимое данного фрагмента.
 */
data class FileChunk(
    val file: File,
    val section: Int,
    val text: String,
)
