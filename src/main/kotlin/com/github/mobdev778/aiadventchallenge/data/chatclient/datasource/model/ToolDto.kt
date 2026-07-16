package com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.model

import kotlinx.serialization.Serializable

/**
 * DTO (Data Transfer Object) для инструмента, доступного в рамках взаимодействия с чат-клиентом.
 *
 * Инструмент представляет собой комбинацию типа (например, `"function"`) и детального описания вызываемой функции.
 * Используется при обмене данными между клиентом и сервером, сериализуется в JSON с помощью `kotlinx.serialization`.
 *
 * @param type Тип инструмента. Как правило, `"function"`, определяющий, что инструмент предназначен для вызова функции.
 * @param function DTO со спецификацией функции, включающей её имя, описание и JSON-схему параметров.
 *
 * @see FunctionDto
 */
@Serializable
data class ToolDto(
    val type: String,
    val function: FunctionDto,
)
