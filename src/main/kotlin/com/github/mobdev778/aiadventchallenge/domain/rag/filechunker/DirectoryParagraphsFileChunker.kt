package com.github.mobdev778.aiadventchallenge.domain.rag.filechunker

import java.io.File

class DirectoryParagraphsFileChunker(
    file: File,
) : FileChunker {

    private val fileChunkers: List<FileChunker> = file.walkTopDown()
        .filter { it.isFile }
        .toList()
        .map { ParagraphsFileChunker(it) }

    private var i = 0

    override fun next(): FileChunk? {
        var next: FileChunk? = null
        while (next == null && i < fileChunkers.size) {
            next = fileChunkers[i].next()
            if (next == null) i++
        }
        return next
    }
}
