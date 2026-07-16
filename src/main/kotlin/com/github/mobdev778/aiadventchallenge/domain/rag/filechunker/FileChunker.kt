package com.github.mobdev778.aiadventchallenge.domain.rag.filechunker

/**
 * Интерфейс для поочередного получения фрагментов файла.
 * Используется в процессе обработки файлов для извлечения логических частей (чанков),
 * например, при подготовке данных для RAG-систем.
 */
interface FileChunker {

    /**
     * Возвращает следующий фрагмент файла или `null`, если фрагментов больше нет.
     *
     * @return следующий [FileChunk] или `null`.
     */
    fun next(): FileChunk?
}
