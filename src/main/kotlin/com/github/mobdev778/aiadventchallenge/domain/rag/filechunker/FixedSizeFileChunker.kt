package com.github.mobdev778.aiadventchallenge.domain.rag.filechunker

import java.io.File
import java.io.RandomAccessFile

/**
 * Реализация [FileChunker], которая разбивает файл на фрагменты фиксированного размера с перекрытием.
 *
 * Используется для подготовки данных в RAG-системах, позволяя извлекать смысловые секции
 * ограниченной длины из исходного файла. Фрагменты могут перекрывать друг друга, что уменьшает
 * потерю контекста на границах разделения. Размер фрагмента и коэффициент перекрытия заданы
 * константами [CHUNK_SIZE_BYTES] и [CHUNK_OVERLAP_FACTOR] соответственно.
 *
 * @property file Исходный файл, который необходимо разбить на фрагменты.
 */
class FixedSizeFileChunker(
    private val file: File,
) : FileChunker {

    val raf = RandomAccessFile(file, "r")
    val buffer = ByteArray(CHUNK_SIZE_BYTES)
    var offset = 0L
    var index = 0

    /**
     * Возвращает следующий фрагмент файла или `null`, если достигнут конец файла.
     *
     * Каждый вызов считывает следующий блок байт заданного размера из файла,
     * преобразует его в строку (с заменой табуляций на пробелы) и возвращает
     * в виде [FileChunk]. После извлечения смещение сдвигается на размер фрагмента,
     * делённый на коэффициент перекрытия, что обеспечивает перекрытие с предыдущим
     * фрагментом.
     *
     * @return следующий [FileChunk] или `null`, если больше фрагментов нет.
     */
    override fun next(): FileChunk? {
        if (offset >= raf.length()) {
            return null
        }

        raf.seek(offset)

        val chunkSize = Math.min(raf.length() - offset, CHUNK_SIZE_BYTES.toLong())
        raf.read(buffer, 0, chunkSize.toInt())

        val text = String(buffer, 0, chunkSize.toInt())
            .replace("\t", "    ")

        return FileChunk(file, index, text).also {
            index++
            offset += buffer.size / CHUNK_OVERLAP_FACTOR
        }
    }

    companion object {
        private const val CHUNK_SIZE_BYTES = 512
        private const val CHUNK_OVERLAP_FACTOR = 2
    }
}
