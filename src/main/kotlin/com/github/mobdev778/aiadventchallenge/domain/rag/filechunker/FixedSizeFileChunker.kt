package com.github.mobdev778.aiadventchallenge.domain.rag.filechunker

import java.io.File
import java.io.RandomAccessFile

class FixedSizeFileChunker(
    private val file: File,
) : FileChunker {

    val raf = RandomAccessFile(file, "r")
    val buffer = ByteArray(CHUNK_SIZE_BYTES)
    var offset = 0L
    var index = 0

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
