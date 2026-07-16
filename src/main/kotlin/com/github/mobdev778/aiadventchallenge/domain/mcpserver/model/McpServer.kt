package com.github.mobdev778.aiadventchallenge.domain.mcpserver.model

import java.util.UUID

/**
 * Модель сервера MCP (Model Context Protocol).
 *
 * Представляет конфигурацию подключения к серверу, включая уникальный идентификатор,
 * признак активности, имя, URL-адрес и флаг локальности.
 *
 * @property id Уникальный идентификатор сервера.
 * @property active Признак активности сервера (доступен/недоступен).
 * @property name Наименование сервера.
 * @property url URL-адрес для подключения к серверу.
 * @property isLocal Флаг, указывающий, является ли сервер локальным.
 */
data class McpServer(
    val id: UUID,
    val active: Boolean,
    val name: String,
    val url: String,
    val isLocal: Boolean,
)
