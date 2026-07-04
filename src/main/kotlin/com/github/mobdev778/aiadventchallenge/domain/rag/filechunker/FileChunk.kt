package com.github.mobdev778.aiadventchallenge.domain.rag.filechunker

import java.io.File

data class FileChunk(
    val file: File,
    val section: Int,
    val text: String,
)
