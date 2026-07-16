package com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.model

import kotlinx.serialization.Serializable

/**
 * DTO для представления вызова функции, используемый при обмене данными с AI-клиентом.
 *
 * @property name Имя вызываемой функции.
 * @property arguments JSON-строка, содержащая аргументы функции.
 */
@Serializable
data class FunctionCallDto(
    val name: String,
    val arguments: String // JSON-строка с аргументами
)
