package com.github.mobdev778.aiadventchallenge.infrastructure.logging

import com.intellij.openapi.application.PathManager
import org.koin.core.annotation.Single
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Single
class PluginLogFileManager {

    private val logsDir: Path by lazy {
        PathManager.getSystemDir()
            .resolve("aiadventchallenge")
            .resolve("logs")
            .also { Files.createDirectories(it) }
    }

    private val fileNameFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val timestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

    fun currentLogFile(): Path {
        return logsDir.resolve("${LocalDate.now().format(fileNameFormatter)}.log")
    }

    fun readCurrentDayLogs(): String {
        val file = currentLogFile()
        if (Files.notExists(file)) return ""
        return Files.readString(file)
    }

    fun append(
        level: String,
        loggerName: String,
        message: String,
        throwable: Throwable? = null,
    ) {
        val formattedMessage = buildString {
            append(LocalDateTime.now().format(timestampFormatter))
            append(" [")
            append(level)
            append("] ")
            append(loggerName)
            append(" - ")
            append(message)
            appendLine()

            throwable?.let {
                append(it.stackTraceToString())
                appendLine()
            }
        }

        print(formattedMessage)

        Files.writeString(
            currentLogFile(),
            formattedMessage,
            StandardCharsets.UTF_8,
            StandardOpenOption.CREATE,
            StandardOpenOption.WRITE,
            StandardOpenOption.APPEND,
        )
    }
}
