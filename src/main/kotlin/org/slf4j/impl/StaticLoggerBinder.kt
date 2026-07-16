package org.slf4j.impl

import com.github.mobdev778.aiadventchallenge.infrastructure.logging.PluginLoggerFactory
import org.slf4j.ILoggerFactory
import org.slf4j.spi.LoggerFactoryBinder

/**
 * Статическая привязка SLF4J, позволяющая фреймворку SLF4J обнаружить и загрузить
 * реализацию фабрики логгеров во время выполнения.
 *
 * Данный класс следует [паттерну привязки SLF4J](https://www.slf4j.org/manual.html#binding):
 * располагается в пакете `org.slf4j.impl` и предоставляет единственный экземпляр
 * [ILoggerFactory] через метод [getSingleton]. При инициализации SLF4J вызывает
 * `StaticLoggerBinder.getSingleton().getLoggerFactory()` и получает готовую к
 * использованию фабрику логгеров — [PluginLoggerFactory].
 *
 * Вся логика непосредственного создания и настройки логгеров делегируется классу
 * [PluginLoggerFactory], что сохраняет данный биндер максимально лаконичным и
 * соответствующим соглашениям SLF4J.
 *
 * @see PluginLoggerFactory
 */
class StaticLoggerBinder private constructor() : LoggerFactoryBinder {

    /**
     * Экземпляр фабрики логгеров, ассоциированный с данной привязкой.
     * Создаётся один раз при конструировании синглтона и используется
     * SLF4J для получения конкретных логгеров.
     */
    private val loggerFactory: ILoggerFactory = PluginLoggerFactory()

    /**
     * Возвращает фабрику логгеров, зарегистрированную в данной привязке.
     *
     * Вызывается фреймворком SLF4J один раз на этапе инициализации.
     *
     * @return текущий экземпляр [ILoggerFactory], реализованный [PluginLoggerFactory]
     */
    override fun getLoggerFactory(): ILoggerFactory = loggerFactory

    /**
     * Возвращает полное квалифицированное имя класса фабрики логгеров в виде строки.
     *
     * Используется SLF4J для диагностических сообщений и логирования версии привязки.
     *
     * @return строковое представление имени класса [PluginLoggerFactory]
     */
    override fun getLoggerFactoryClassStr(): String = PluginLoggerFactory::class.java.name

    companion object {
        /**
         * Единственный экземпляр [StaticLoggerBinder], создаваемый в момент загрузки класса.
         * Реализует потокобезопасный паттерн «синглтон» на основе инициализации companion-объекта,
         * гарантируемой JVM.
         */
        private val singleton = StaticLoggerBinder()

        /**
         * Возвращает единственный экземпляр данного биндера.
         *
         * Аннотирован [@JvmStatic][JvmStatic] для корректной генерации статического метода
         * в байт-коде, что необходимо для обнаружения привязки фреймворком SLF4J через рефлексию.
         *
         * @return синглтон [StaticLoggerBinder]
         */
        @JvmStatic
        fun getSingleton(): StaticLoggerBinder = singleton

        /**
         * Версия API SLF4J, с которой совместима данная привязка.
         *
         * Значение `"1.7.36"` сообщает фреймворку SLF4J, что биндер реализован
         * для версии API 1.7.x и использует соответствующие контракты и соглашения.
         * Данная константа считывается SLF4J на этапе проверки совместимости.
         */
        const val REQUESTED_API_VERSION: String = "1.7.36"
    }
}
