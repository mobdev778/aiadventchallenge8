package com.github.mobdev778.aiadventchallenge.domain.profile.model

import java.util.UUID

/**
 * Модель данных профиля пользователя, определяющая его идентификатор, имя,
 * содержимое (инструкции/правила) и статус выбранного профиля.
 *
 * @property id Уникальный идентификатор профиля.
 * @property name Отображаемое имя профиля.
 * @property content Содержимое профиля в формате Markdown, описывающее контекст и стиль поведения ассистента.
 * @property isSelected Флаг, указывающий, выбран ли профиль в данный момент как активный.
 */
data class Profile(
    val id: UUID,
    val name: String,
    val content: String,
    val isSelected: Boolean,
) {
    companion object {
        /**
         * Дефолтный профиль с общим предустановленным контекстом,
         * который используется при отсутствии пользовательских профилей.
         */
        val default: Profile = Profile(
            id = UUID(0, 0),
            name = "Дефолтный",
            content = """
                # USER PERSONALIZATION PROFILE (General Purpose)
                - Context Awareness: Adapt to the user's intent. If the query is about software development, behave like a Middle Software Engineer (write clean, production-ready, idiomatic code). For general, non-technical queries, behave as a helpful, practical, and empathetic everyday assistant.
                - Communication Style: Balanced, direct, and concise. Avoid over-explaining obvious things, but provide enough context to make the answer complete and actionable.
                - Constraints: Avoid slang or overly academic jargon. Always structure responses logically (use bullet points or numbered lists where appropriate).
            """.trimIndent(),
            isSelected = true,
        )
    }
}
