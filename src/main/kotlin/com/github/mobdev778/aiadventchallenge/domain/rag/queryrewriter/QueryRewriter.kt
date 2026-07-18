package com.github.mobdev778.aiadventchallenge.domain.rag.queryrewriter

import com.github.mobdev778.aiadventchallenge.domain.chatclient.ChatClient
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Message
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Role
import com.github.mobdev778.aiadventchallenge.domain.settings.SettingsInteractor
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single

/**
 * Сервис перезаписи пользовательских запросов для Retrieval-Augmented Generation (RAG).
 *
 * Генерирует альтернативные формулировки исходного запроса с помощью языковой модели,
 * чтобы повысить охват и точность поиска релевантных документов в векторном хранилище.
 * Использует [ChatClient] для отправки инструкции модели, получает список переписанных
 * вариантов в формате JSON и десериализует его через [RewrittenQueriesResponse].
 *
 * Аннотирован [@Single][Single] (Koin), поэтому во всём приложении существует
 * единственный экземпляр.
 *
 * @property chatClient Фасад чат-клиента для выполнения запросов к языковой модели.
 * @property settingsInteractor Интерактор для получения текущих настроек приложения,
 *                              особенно API-ключа, базового URL и имени модели.
 * @property json Настроенный экземпляр kotlinx.serialization.Json для десериализации ответа.
 */
@Single
class QueryRewriter(
    private val chatClient: ChatClient,
    private val settingsInteractor: SettingsInteractor,
    private val json: Json,
) {

    /**
     * Формирует заданное количество альтернативных формулировок пользовательского запроса.
     *
     * Алгоритм:
     * 1. Получает актуальные настройки приложения ([AppSettings]) через [settingsInteractor].
     * 2. Конструирует системный промпт, инструктирующий модель переписать запрос определённым
     *    образом и вернуть результат в виде строго заданной JSON-структуры.
     * 3. Отправляет запрос в языковую модель через [chatClient].
     * 4. Извлекает текстовое содержимое первого ответа, очищает его от возможных markdown-тегов
     *    и десериализует в объект [RewrittenQueriesResponse].
     * 5. Возвращает полученный список переписанных запросов. В случае ошибки парсинга (например,
     *    если модель ответила невалидным JSON) возвращает список из одного исходного запроса как fallback.
     *
     * @param query Исходный пользовательский запрос для перезаписи.
     * @param count Количество альтернативных формулировок, которое должна сгенерировать модель.
     * @return Список строк — переписанных вариантов запроса. В случае сбоя возвращает `listOf(query)`.
     */
    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    suspend fun getQueries(query: String, count: Int): List<String> {
        val appSettings = settingsInteractor.observeSettings().first()

        val systemPrompt =
            "You are an expert search-query optimization assistant " +
                    "for Retrieval-Augmented Generation (RAG) systems. " +
                    "Your task is to perform \"query rewriting\" to improve search recall and precision.\n" +
                    "\n" +
                    "        Analyze the user's input query and generate exactly " +
                    "$count distinct variations of it. \n" +
                    "\n" +
                    "        Follow these rewriting strategies:\n" +
                    "        1. Rephrasing & Synonyms: Use different terminology, technical synonyms, " +
                    "or alternative phrasing while keeping the original intent.\n" +
                    "        2. Conceptual Expansion: Breakdown the query into core concepts " +
                    "or add implicit keywords that a relevant document would likely contain.\n" +
                    "        3. Search Engine Style: Convert the natural language question into a concise, " +
                    "keyword-driven search string (like a Google or vector search query).\n" +
                    "\n" +
                    "        Output MUST be a valid JSON object with a single key \"rewritten_queries\" " +
                    "containing an array of exactly 3 strings. " +
                    "Do not include any explanations or markdown formatting outside the JSON.\n" +
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
            parsed.rewrittenQueries
        } catch (e: Exception) {
            // Логируем ошибку парсинга и возвращаем исходный запрос в качестве фолбека
            listOf(query)
        }
    }
}
