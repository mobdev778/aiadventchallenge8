package org.slf4j.impl

import com.github.mobdev778.aiadventchallenge.infrastructure.logging.PluginLoggerFactory
import org.slf4j.ILoggerFactory
import org.slf4j.spi.LoggerFactoryBinder

class StaticLoggerBinder private constructor() : LoggerFactoryBinder {

    private val loggerFactory: ILoggerFactory = PluginLoggerFactory()

    override fun getLoggerFactory(): ILoggerFactory = loggerFactory

    override fun getLoggerFactoryClassStr(): String = PluginLoggerFactory::class.java.name

    companion object {
        private val singleton = StaticLoggerBinder()

        @JvmStatic
        fun getSingleton(): StaticLoggerBinder = singleton

        const val REQUESTED_API_VERSION: String = "1.7.36"
    }
}
