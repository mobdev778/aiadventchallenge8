package com.github.mobdev778.aiadventchallenge.presentation.addmcpserverscreen.model

import androidx.compose.runtime.Immutable

/**
 * Состояние экрана добавления MCP-сервера.
 * Содержит поля ввода: имя сервера и его URL.
 *
 * @property name введённое пользователем имя сервера
 * @property url введённый пользователем URL сервера
 */
@Immutable
data class AddMcpServerScreenState(
    val name: String = "",
    val url: String = "",
)
