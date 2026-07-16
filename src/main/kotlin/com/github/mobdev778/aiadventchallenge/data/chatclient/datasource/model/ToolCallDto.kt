package com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.model

import kotlinx.serialization.Serializable

/**
 * DTO для представления вызова инструмента (tool call) в ответе AI-модели.
 * Содержит идентификатор вызова, его тип и данные о вызываемой функции.
 *
 * @property id Уникальный идентификатор вызова инструмента.
 * @property type Тип вызываемого инструмента (например, "function").
 * @property function DTO с именем и аргументами функции, которую необходимо вызвать.
 */
@Serializable
data class ToolCallDto(
    val id: String,
    val type: String,
    val function: FunctionCallDto
)
