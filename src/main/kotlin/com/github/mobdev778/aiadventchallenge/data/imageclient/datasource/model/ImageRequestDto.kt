package com.github.mobdev778.aiadventchallenge.data.imageclient.datasource.model

import kotlinx.serialization.Serializable

/**
 * DTO-объект запроса к API генерации изображений.
 *
 * Представляет собой сериализуемую модель данных, которая отправляется
 * в теле HTTP-запроса к внешнему сервису генерации изображений.
 * Содержит параметры, определяющие модель ИИ, текстовое описание
 * желаемого изображения и его размер.
 *
 * @property model Идентификатор модели ИИ, используемой для генерации изображения.
 * @property prompt Текстовое описание (промпт) желаемого изображения.
 * @property size Размер генерируемого изображения в формате, поддерживаемом API
 *                (например, "1024x1024").
 */
@Serializable
class ImageRequestDto(
    val model: String,
    val prompt: String,
    val size: String,
)
