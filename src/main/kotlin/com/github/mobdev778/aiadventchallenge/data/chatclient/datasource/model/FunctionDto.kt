package com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * DTO (Data Transfer Object) для описания функции, доступной к вызову в рамках взаимодействия с моделью.
 *
 * Используется в моделях данных клиента чата для передачи спецификации функций, включая их имена,
 * описания и ожидаемые параметры. Объект сериализуется в JSON с помощью kotlinx.serialization.
 *
 * @param name Уникальное имя функции, по которому она будет идентифицироваться при вызове.
 * @param description Необязательное текстовое описание предназначения и поведения функции.
 * @param parameters JSON-схема параметров функции, соответствующая спецификации JSON Schema.
 *                  Определяет типы, обязательность и ограничения для входных данных функции.
 */
@Serializable
data class FunctionDto(
    val name: String,
    val description: String?,
    val parameters: JsonObject,
)
