package com.github.mobdev778.aiadventchallenge.infrastructure.logging

import org.slf4j.LoggerFactory

class PluginUncaughtExceptionHandler(
    private val previousHandler: Thread.UncaughtExceptionHandler?,
) : Thread.UncaughtExceptionHandler {

    private val logger = LoggerFactory.getLogger("UncaughtException")

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        logger.error("Uncaught exception in thread ${thread.name}", throwable)
        previousHandler?.uncaughtException(thread, throwable)
    }
}
