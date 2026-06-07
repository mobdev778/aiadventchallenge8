package com.github.mobdev778.aiadventchallenge.domain.imagegenerator.svg

import com.embeddings.rag.BuildConfig
import com.github.mobdev778.aiadventchallenge.domain.imagegenerator.ImageGenerator
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.model.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.model.ChatResponse
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.model.Message
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.ChatClient
import com.github.mobdev778.aiadventchallenge.domain.openai.chat.model.Role
import org.apache.batik.transcoder.TranscoderInput
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream

class SvgImageGenerator(
    private val client: ChatClient,
) : ImageGenerator {

    /**
     * Отправляет запрос к LLM и рисует SVG в виде BufferedImage.
     */
    override suspend fun generateImage(
        temperature: Double,
        userPrompt: String,
    ): BufferedImage {
        // Системный промпт. Запрещает markdown-разметку (```xml)
        val systemPrompt = """
            You are an elite, world-class SVG artist specializing in hyper-realistic vector graphics, digital painting, and advanced light simulation.
        Your task is to output a highly detailed, volumetric, and lifelike SVG image based on the user's request. Do NOT make it flat or look like a cartoon or children's drawing.

        TECHNICAL REQUIREMENTS TO ACHIEVE PHOTOREALISM:
        1. Use dozens of overlapping paths to mimic smooth skin, fur textures, sleek metallic reflections, or soft organic gradients.

        CRITICAL RULES:
        1. Return ONLY valid, raw SVG code starting with <svg> and ending with </svg>.
        2. Do NOT wrap the code in markdown code blocks like ```xml or ```html.
        3. Ensure strict XML syntax. Do not include any conversational text, warnings, or notes.
        4. Set appropriate 'viewBox', 'width', and 'height' (e.g., 512x512).
        5. Use only "org.apache.batik.transcoder" compatible SVG elements and attributes.
        6. Avoid 'stop-color' and 'rgba' attributes.
        """.trimIndent()

        val response: ChatResponse = client.execute(
            request = ChatRequest(
                model = BuildConfig.MODEL,
                temperature = temperature,
                messages = listOf(
                    Message(role = Role.System, content = systemPrompt),
                    Message(role = Role.User, content = "Create a detailed SVG image of: $userPrompt")
                )
            )
        )
        val rawContent = response.choices.firstOrNull()?.message?.content
            ?: throw IllegalStateException("Empty response from LLM")

        val filtered = cleanMarkdownCodeBlocks(rawContent)

        return renderSvgToBufferedImage(filtered)
    }

    private fun cleanMarkdownCodeBlocks(content: String): String {
        var clean = content
        if (clean.startsWith("```")) {
            clean = clean.substringAfter("\n")
        }
        if (clean.endsWith("```")) {
            clean = clean.substringBeforeLast("```")
        }
        return clean.trim()
    }

    fun renderSvgToBufferedImage(svgCode: String): BufferedImage {
        val inputStream = ByteArrayInputStream(svgCode.toByteArray(Charsets.UTF_8))
        val input = TranscoderInput(inputStream)

        val transcoder = BufferedImageTranscoder()
        transcoder.transcode(input, null)
        return transcoder.bufferedImage ?: throw IllegalStateException("Failed to rasterize SVG")
    }
}