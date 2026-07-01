package com.github.mobdev778.aiadventchallenge.domain.rag.queryrewriter

import com.github.mobdev778.aiadventchallenge.domain.chatclient.ChatClient
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Message
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Role
import com.github.mobdev778.aiadventchallenge.domain.settings.SettingsInteractor
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

@Single
class QueryRewriter(
    private val chatClient: ChatClient,
    private val settingsInteractor: SettingsInteractor,
    private val json: Json,
) {

    suspend fun getQueries(query: String, count: Int): List<String> {
        val appSettings = settingsInteractor.observeSettings().first()

        val systemPrompt =
            "You are an expert search-query optimization assistant for Retrieval-Augmented Generation (RAG) systems. Your task is to perform \"query rewriting\" to improve search recall and precision.\n" +
                    "\n" +
                    "        Analyze the user's input query and generate exactly $count distinct variations of it. \n" +
                    "\n" +
                    "        Follow these rewriting strategies:\n" +
                    "        1. Rephrasing & Synonyms: Use different terminology, technical synonyms, or alternative phrasing while keeping the original intent.\n" +
                    "        2. Conceptual Expansion: Breakdown the query into core concepts or add implicit keywords that a relevant document would likely contain.\n" +
                    "        3. Search Engine Style: Convert the natural language question into a concise, keyword-driven search string (like a Google or vector search query).\n" +
                    "\n" +
                    "        Output MUST be a valid JSON object with a single key \"rewritten_queries\" containing an array of exactly 3 strings. Do not include any explanations or markdown formatting outside the JSON.\n" +
                    "\n" +
                    "        Example Output Format (if number of variations is 3):\n" +
                    "        {\n" +
                    "            \"rewritten_queries\": [\n" +
                    "            \"Variation 1 here\",\n" +
                    "            \"Variation 2 here\",\n" +
                    "            \"Variation 3 here\"\n" +
                    "            ]\n" +
                    "        }"
        val response = chatClient.execute(
            ChatRequest(
                model = appSettings.baseModel,
                messages = listOf(
                    Message(
                        Role.System,
                        systemPrompt,
                    ),
                    Message(
                        Role.User,
                        "Rewrite the following user query into 3 variations for RAG retrieval:\n\n" +
                                "Query: \"$query\"\n",
                    )
                )
            )
        )
        val content = response.choices.firstOrNull()?.message?.content
            ?: return emptyList()

        return try {
            // Очищаем от возможных markdown-тегов ```json ... ```, если модель их забыла убрать
            val cleanJson = content.trim()
                .removePrefix("```json")
                .removeSuffix("```")
                .trim()

            val parsed = json.decodeFromString<RewrittenQueriesResponse>(cleanJson)
            parsed.rewritten_queries
        } catch (e: Exception) {
            // Логируем ошибку парсинга и возвращаем исходный запрос в качестве фолбека
            listOf(query)
        }
    }
}