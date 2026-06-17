package com.github.mobdev778.aiadventchallenge.domain.profile.model

import java.util.UUID

data class Profile(
    val id: UUID,
    val name: String,
    val content: String,
    val isSelected: Boolean,
) {
    companion object {
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
