package com.github.mobdev778.aiadventchallenge.domain.imagegenerator.gpt

import com.github.mobdev778.aiadventchallenge.domain.imagegenerator.ImageGenerator
import com.github.mobdev778.aiadventchallenge.domain.chatclient.ChatClient
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Message
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Role
import com.github.mobdev778.aiadventchallenge.domain.imageclient.ImageClient
import com.github.mobdev778.aiadventchallenge.domain.imageclient.model.ImageRequest
import org.koin.core.annotation.Single
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.util.Base64
import javax.imageio.ImageIO

@Single
class GptImageGenerator(
    private val chatClient: ChatClient,
    private val imageClient: ImageClient,
) : ImageGenerator {

    val systemPrompt =
        """
You are a professional prompt engineer and visual director. Your sole objective is to transform short, simple, or abstract user inputs into highly detailed, cinematic, and visually stunning prompts for the 'gpt-image-2' image generator.

        ### ENRICHMENT RULES:
        1. PRESERVE THE ESSENCE: Never alter the core meaning, characters, or main action intended by the user. Expand and detail them instead.
        2. DEFAULT STYLISTICS: If the user does not specify a style (e.g., 'anime', 'vector', 'drawing'), always default to a photorealistic cinematic style.
        3. AVOID BUZZWORDS: Do not use empty terms like 'hyperrealistic', '4k', '8k', or 'masterpiece'. Instead, describe details physically (e.g., 'fine skin texture', 'visible pores', 'subtle dust motes caught in the light beam').
        4. OUTPUT LANGUAGE: Always generate the final prompt in English, using commas to separate descriptive clauses.

        ### PERFECT PROMPT ARCHITECTURE:
        Every output must synthetically integrate the following aspects:
        - Subject: Detailed description (age, clothing, facial expression, posture, skin/material texture).
        - Environment/Background: Location, time of day, weather, atmosphere, and minor background elements.
        - Lighting: Direction and type of light (e.g., 'volumetric golden hour light', 'neon cyberpunk glow', 'dramatic chiaroscuro', 'soft studio lighting').
        - Camera & Composition: Shot type, framing, and lens specs (e.g., 'Close-up shot, 35mm lens, f/1.8, shallow depth of field', 'Wide-angle landscape composition, rule of thirds').
        - Color Palette: Dominant hues and overall tonality (e.g., 'muted earthy tones', 'vibrant neon synthwave palette', 'monochromatic with a splash of crimson').

        ### OUTPUT FORMAT:
        Output ONLY the final enriched prompt in English inside a single code block. Do not include any greetings, explanations, introduction, or conversational filler. Only the ready-to-copy prompt text.                
        """.trimIndent()

    override suspend fun generateImage(
        temperature: Double,
        userPrompt: String,
    ): BufferedImage {
        val enrichedPrompt = chatClient.execute(
            ChatRequest(
                model = "gpt-4o",
                messages = listOf(
                    Message(Role.System, systemPrompt),
                    Message(Role.User, userPrompt)
                ),
                temperature = temperature,
            )
        ).choices.firstOrNull()?.message?.content ?: userPrompt

        val response = imageClient.execute(
            ImageRequest(
                model = "gpt-image-1",
                prompt = enrichedPrompt,
                size = "1024x1024",
            )
        )

        val base64String = response.data.firstOrNull()?.b64Json
            ?: throw IllegalStateException("API OpenAI не вернул Base64 данные изображения.")

        // Декодируем в BufferedImage
        val imageBytes = Base64.getDecoder().decode(base64String)
        return ByteArrayInputStream(imageBytes).use { inputStream ->
            ImageIO.read(inputStream) ?: throw IllegalStateException("Не удалось преобразовать байты в BufferedImage.")
        }
    }
}