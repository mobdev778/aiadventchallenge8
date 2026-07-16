package com.github.mobdev778.aiadventchallenge.domain.imageclient.model

/**
 * Запрос на генерацию изображения с использованием заданной модели.
 *
 * @property model Идентификатор используемой модели.
 * @property prompt Текстовое описание желаемого изображения.
 * @property size Размер генерируемого изображения (например, "1024x1024").
 */
data class ImageRequest(
    val model: String,
    val prompt: String,
    val size: String,
)
