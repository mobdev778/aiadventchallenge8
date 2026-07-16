package com.github.mobdev778.aiadventchallenge.domain.rag.filechunker

import com.github.mobdev778.aiadventchallenge.presentation.rag.addragdocumentscreen.model.AddRagDocumentScreenState
import java.io.File

/**
 * Фабрика для создания объектов [FileChunker] в зависимости от выбранной стратегии
 * разбиения файла на фрагменты.
 *
 * Инкапсулирует логику выбора конкретной реализации чанкера ([FixedSizeFileChunker] или
 * [ParagraphsFileChunker]) на основе стратегии, заданной пользователем в
 * [AddRagDocumentScreenState.ChunkingStrategy]. Таким образом, клиентскому коду не нужно
 * знать о конкретных классах чанкеров, а только о стратегии и интерфейсе [FileChunker].
 */
class FileChunkerFactory {

    /**
     * Создаёт и возвращает экземпляр [FileChunker] для указанного файла с использованием
     * заданной стратегии разбиения.
     *
     * @param file исходный файл, который требуется разбить на фрагменты.
     * @param strategy выбранная стратегия чанкинга, определённая в модели
     *                 [AddRagDocumentScreenState.ChunkingStrategy].
     * @return новый объект [FileChunker], готовый к поочередному извлечению фрагментов.
     */
    fun create(
        file: File,
        strategy: AddRagDocumentScreenState.ChunkingStrategy
    ): FileChunker {
        return when (strategy) {
            AddRagDocumentScreenState.ChunkingStrategy.FixedSize -> when {
                file.isDirectory -> DirectoryFixedSizeFileChunker(file)
                else -> FixedSizeFileChunker(file)
            }
            AddRagDocumentScreenState.ChunkingStrategy.Paragraphs -> when {
                file.isDirectory -> DirectoryParagraphsFileChunker(file)
                else -> ParagraphsFileChunker(file)
            }
        }
    }
}
