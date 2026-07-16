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

/**
 * Компонент-синглтон для управления лог-файлами плагина.
 * Обеспечивает запись структурированных логов в файл и дублирование в стандартный вывод.
 * Использует системную директорию IntelliJ (через [PathManager]) для создания каталогов и файлов,
 * разделяя логи по дням. Предназначен для диагностики и мониторинга работы плагина.
 */
@Single
class PluginLogFileManager {

    /**
     * Корневая директория для логов, создаваемая в системной папке IntelliJ.
     * При первом обращении создаются все необходимые родительские каталоги.
     */
    private val logsDir: Path by lazy {
        PathManager.getSystemDir()
            .resolve("aiadventchallenge")
            .resolve("logs")
            .also { Files.createDirectories(it) }
    }

    /** Форматтер для имени файла лога (ISO-дата, например "2025-02-17"). */
    private val fileNameFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    /** Форматтер для временной метки записи в логе. */
    private val timestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

    /**
     * Возвращает путь к лог-файлу за текущий день.
     *
     * @return [Path] к файлу лога, имя которого формируется по шаблону yyyy-MM-dd.log.
     */
    fun currentLogFile(): Path {
        return logsDir.resolve("${LocalDate.now().format(fileNameFormatter)}.log")
    }

    /**
     * Читает и возвращает содержимое лог-файла за текущий день.
     * Если файл отсутствует, возвращает пустую строку.
     *
     * @return строка со всеми записями текущего дня или пустая строка.
     */
    fun readCurrentDayLogs(): String {
        val file = currentLogFile()
        if (Files.notExists(file)) return ""
        return Files.readString(file)
    }

    /**
     * Добавляет новую запись в лог-файл текущего дня и дублирует её в консоль.
     * Запись включает временную метку, уровень, имя логгера, сообщение и (опционально) стектрейс исключения.
     *
     * @param level уровень важности (INFO, WARN, ERROR и т.п.).
     * @param loggerName идентификатор логгера/компонента, из которого поступило сообщение.
     * @param message текстовое описание события.
     * @param throwable необязательное исключение, для которого будет выведен стектрейс.
     */
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
