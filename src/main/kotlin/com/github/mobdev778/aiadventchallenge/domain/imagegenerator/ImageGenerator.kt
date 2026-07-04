package com.github.mobdev778.aiadventchallenge.domain.imagegenerator

import java.awt.image.BufferedImage

interface ImageGenerator {

    suspend fun generateImage(
        temperature: Double,
        userPrompt: String,
    ): BufferedImage
}
