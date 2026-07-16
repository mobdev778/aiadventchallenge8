package com.github.mobdev778.aiadventchallenge.infrastructure.logging

import org.slf4j.LoggerFactory

/**
 * Обработчик неперехваченных исключений для плагина.
 *
 * Перехватывает исключения, не обработанные в потоках, логирует их с помощью SLF4J,
 * а затем передает управление предыдущему обработчику (если он был задан).
 * Это позволяет сохранить цепочку обработки исключений и добавить централизованное
 * логирование для всех необработанных ошибок в приложении.
 *
 * @param previousHandler предыдущий обработчик неперехваченных исключений,
 *                        которому будет передано исключение после логирования.
 *                        Может быть `null`, если такого обработчика нет.
 */
class PluginUncaughtExceptionHandler(
    private val previousHandler: Thread.UncaughtExceptionHandler?,
) : Thread.UncaughtExceptionHandler {

    private val logger = LoggerFactory.getLogger("UncaughtException")

    /**
     * Вызывается, когда в потоке возникает неперехваченное исключение.
     *
     * Логирует ошибку с уровнем `ERROR` и информацией о потоке, а затем
     * передает исключение предыдущему обработчику (если он задан).
     *
     * @param thread поток, в котором произошло исключение.
     * @param throwable возникшее исключение.
     */
    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        logger.error("Uncaught exception in thread ${thread.name}", throwable)
        previousHandler?.uncaughtException(thread, throwable)
    }
}
