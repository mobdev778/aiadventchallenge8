package com.github.mobdev778.aiadventchallenge.domain.rag.filechunker

import java.io.File
import java.nio.charset.StandardCharsets

class ParagraphsFileChunker(val file: File) : FileChunker {

    val bufferedReader = file.bufferedReader(StandardCharsets.UTF_8)

    private var cachedLine: String? = null
    var index = 0

    override fun next(): FileChunk? {
        var paragraph: String?
        do {
            paragraph = readParagraph()
        } while (paragraph?.isBlank() == true)

        if (paragraph != null) {
            return FileChunk(file, index, paragraph).also {
                index++
            }
        } else {
            bufferedReader.close()
            return null
        }
    }

    private fun readParagraph(): String? {
        val firstLine = readLine() ?: return null

        val builder = StringBuilder()
        builder.append(firstLine)

        val prefixSize = getSpacePrefixSize(firstLine)

        while (true) {
            val line = readLine() ?: break
            if (prefixSize == getSpacePrefixSize(line)) {
                if (builder.length > 0) {
                    builder.append("\n")
                }
                builder.append(line)
            } else {
                pushBackLine(line)
                break
            }
        }
        return builder.toString()
    }

    private fun readLine(): String? {
        if (cachedLine != null) {
            val result = cachedLine
            cachedLine = null
            return result
        }
        val line = bufferedReader.readLine() ?: return null
        return line.replace("\t", "    ")
    }

    private fun pushBackLine(line: String) {
        cachedLine = line
    }

    private fun getSpacePrefixSize(line: String): Int {
        var result = 0
        for (c in line) {
           if (c == ' ') result++ else break
        }
        return result
    }
}