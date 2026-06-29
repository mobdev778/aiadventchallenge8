package com.github.mobdev778.aiadventchallenge.domain.rag.filechunker

interface FileChunker {

    fun next(): FileChunk?
}