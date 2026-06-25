package com.github.mobdev778.aiadventchallenge.infrastructure.logging

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.slf4j.ILoggerFactory
import org.slf4j.Logger
import org.slf4j.helpers.MarkerIgnoringBase
import java.util.concurrent.ConcurrentHashMap

class PluginLoggerFactory : ILoggerFactory, KoinComponent {

    private val logFileManager: PluginLogFileManager by inject()
    private val loggers = ConcurrentHashMap<String, Logger>()

    override fun getLogger(name: String): Logger {
        return loggers.computeIfAbsent(name) {
            PluginSlf4jLogger(
                name = it,
                logFileManager = logFileManager,
            )
        }
    }
}

private class PluginSlf4jLogger(
    name: String,
    private val logFileManager: PluginLogFileManager,
) : MarkerIgnoringBase() {

    init {
        this.name = name
    }

    override fun isTraceEnabled() = true
    override fun isDebugEnabled() = true
    override fun isInfoEnabled() = true
    override fun isWarnEnabled() = true
    override fun isErrorEnabled() = true

    override fun trace(msg: String?) = log("TRACE", msg)
    override fun trace(format: String?, arg: Any?) = log("TRACE", formatMessage(format, arg))
    override fun trace(format: String?, arg1: Any?, arg2: Any?) = log("TRACE", formatMessage(format, arg1, arg2))
    override fun trace(format: String?, vararg arguments: Any?) = log("TRACE", formatMessage(format, *arguments))
    override fun trace(msg: String?, t: Throwable?) = log("TRACE", msg, t)

    override fun debug(msg: String?) = log("DEBUG", msg)
    override fun debug(format: String?, arg: Any?) = log("DEBUG", formatMessage(format, arg))
    override fun debug(format: String?, arg1: Any?, arg2: Any?) = log("DEBUG", formatMessage(format, arg1, arg2))
    override fun debug(format: String?, vararg arguments: Any?) = log("DEBUG", formatMessage(format, *arguments))
    override fun debug(msg: String?, t: Throwable?) = log("DEBUG", msg, t)

    override fun info(msg: String?) = log("INFO", msg)
    override fun info(format: String?, arg: Any?) = log("INFO", formatMessage(format, arg))
    override fun info(format: String?, arg1: Any?, arg2: Any?) = log("INFO", formatMessage(format, arg1, arg2))
    override fun info(format: String?, vararg arguments: Any?) = log("INFO", formatMessage(format, *arguments))
    override fun info(msg: String?, t: Throwable?) = log("INFO", msg, t)

    override fun warn(msg: String?) = log("WARN", msg)
    override fun warn(format: String?, arg: Any?) = log("WARN", formatMessage(format, arg))
    override fun warn(format: String?, arg1: Any?, arg2: Any?) = log("WARN", formatMessage(format, arg1, arg2))
    override fun warn(format: String?, vararg arguments: Any?) = log("WARN", formatMessage(format, *arguments))
    override fun warn(msg: String?, t: Throwable?) = log("WARN", msg, t)

    override fun error(msg: String?) = log("ERROR", msg)
    override fun error(format: String?, arg: Any?) = log("ERROR", formatMessage(format, arg))
    override fun error(format: String?, arg1: Any?, arg2: Any?) = log("ERROR", formatMessage(format, arg1, arg2))
    override fun error(format: String?, vararg arguments: Any?) = log("ERROR", formatMessage(format, *arguments))
    override fun error(msg: String?, t: Throwable?) = log("ERROR", msg, t)

    private fun log(level: String, message: String?, throwable: Throwable? = null) {
        logFileManager.append(
            level = level,
            loggerName = name,
            message = message.orEmpty(),
            throwable = throwable,
        )
    }

    private fun formatMessage(format: String?, vararg arguments: Any?): String {
        if (format == null) return ""
        if (arguments.isEmpty()) return format

        val builder = StringBuilder()
        var searchStart = 0
        var argumentIndex = 0

        while (true) {
            val placeholderIndex = format.indexOf("{}", startIndex = searchStart)
            if (placeholderIndex < 0) {
                builder.append(format.substring(searchStart))
                break
            }

            builder.append(format.substring(searchStart, placeholderIndex))
            if (argumentIndex < arguments.size) {
                builder.append(arguments[argumentIndex])
                argumentIndex++
            } else {
                builder.append("{}")
            }
            searchStart = placeholderIndex + 2
        }

        if (argumentIndex < arguments.size) {
            val remaining = arguments.drop(argumentIndex).joinToString(prefix = " [", postfix = "]")
            builder.append(remaining)
        }

        return builder.toString()
    }
}
