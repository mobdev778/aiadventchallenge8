package com.github.mobdev778.aiadventchallenge.domain.rag.filechunker

import java.io.File
import java.io.RandomAccessFile

class FixedSizeFileChunker(
    private val file: File,
) : FileChunker {

    val raf = RandomAccessFile(file, "r")
    val buffer = ByteArray(1024)
    var offset = 0L
    var index = 0

    override fun next(): FileChunk? {
        if (offset >= raf.length()) {
            return null
        }

        raf.seek(offset)

        val chunkSize = Math.min(raf.length() - offset, 1024L)
        raf.read(buffer, 0, chunkSize.toInt())

        val text = String(buffer, 0, chunkSize.toInt())
            .replace("\t", "    ")

        return FileChunk(file, index, text).also {
            index++
            offset += buffer.size / 2
        }
    }
}