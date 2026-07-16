package com.github.mobdev778.aiadventchallenge.presentation.app

import com.intellij.DynamicBundle
import org.jetbrains.annotations.PropertyKey
import java.util.function.Supplier

private const val BUNDLE = "messages.MessageBundle"

/**
 * Внутренний объект, предоставляющий доступ к локализованным сообщениям.
 *
 * Использует [DynamicBundle] для загрузки строк из ресурсного бандла `messages.MessageBundle`.
 * Позволяет получать как сразу сформированные строки, так и ленивые поставщики сообщений.
 */
internal object MessageBundle {
    private val instance = DynamicBundle(MessageBundle::class.java, BUNDLE)

    /**
     * Возвращает локализованное сообщение по заданному ключу с подстановкой переданных параметров.
     *
     * @param key ключ сообщения в ресурсном бандле [BUNDLE]
     * @param params опциональные параметры для форматирования сообщения
     * @return отформатированная строка сообщения
     */
    @JvmStatic
    fun message(key: @PropertyKey(resourceBundle = BUNDLE) String, vararg params: Any?): String {
        return instance.getMessage(key, *params)
    }

    /**
     * Возвращает ленивый поставщик ([Supplier]) локализованного сообщения, который будет вычислен
     * при первом вызове [Supplier.get].
     *
     * @param key ключ сообщения в ресурсном бандле [BUNDLE]
     * @param params опциональные параметры для форматирования сообщения
     * @return поставщик, предоставляющий отформатированное сообщение
     */
    @JvmStatic
    fun lazyMessage(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any?): Supplier<String> {
        return instance.getLazyMessage(key, *params)
    }
}
