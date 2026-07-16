package com.github.mobdev778.aiadventchallenge.data.mcpserver.datasource.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room-сущность, представляющая MCP-сервер в базе данных.
 *
 * Хранит основные сведения о сервере: его идентификатор, признак активности,
 * человекочитаемое имя и URL-адрес для подключения. Используется в качестве
 * источника данных в локальном хранилище приложения.
 *
 * @property id Уникальный идентификатор MCP-сервера (первичный ключ).
 * @property active Флаг, указывающий, является ли сервер активным в данный момент.
 * @property name Отображаемое имя сервера.
 * @property url URL-адрес, по которому доступен MCP-сервер.
 */
@Entity(tableName = "mcp_servers")
data class McpServerEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "active")
    val active: Boolean,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "url")
    val url: String,
)
