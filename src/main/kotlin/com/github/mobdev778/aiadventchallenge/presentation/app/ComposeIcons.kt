package com.github.mobdev778.aiadventchallenge.presentation.app

import com.intellij.ui.IconManager

/**
 * Объект-синглтон, предоставляющий иконки, используемые в плагине.
 * Содержит предварительно загруженные иконки для различных компонентов пользовательского интерфейса.
 */
@Suppress("unused")
object ComposeIcons {
    /**
     * Иконка для окна инструментов Compose.
     * Загружается из ресурсов плагина и используется для отображения в тулбаре окна инструментов.
     */
    @JvmField
    val ComposeToolWindow =
        IconManager.getInstance().getIcon("/icons/icon.png", javaClass.getClassLoader())
}
