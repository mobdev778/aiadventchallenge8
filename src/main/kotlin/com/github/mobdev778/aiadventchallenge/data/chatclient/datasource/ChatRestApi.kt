package com.github.mobdev778.aiadventchallenge.data.chatclient.datasource

import com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.model.ChatRequestDto
import com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.model.ChatResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit-интерфейс для взаимодействия с REST API чат-комплишнов (chat completions).
 *
 * Предоставляет методы для отправки запросов генерации сообщений к языковым моделям (например, GPT-4o).
 * Используется в слое данных для выполнения сетевых вызовов и получения ответов модели.
 */
interface ChatRestApi {

    /**
     * Отправляет POST-запрос к эндпоинту `/chat/completions` с телом, содержащим данные запроса чата.
     *
     * @param request объект [ChatRequestDto], включающий:
     * - `model` — название модели (например, "gpt-4o");
     * - `reasoningEffort` — уровень усилий на внутренние рассуждения (для o-серии);
     * - `messages` — история диалога;
     * - `tools` — список доступных инструментов (MCP);
     * - `toolChoice` — режим выбора инструмента (при наличии инструментов — "auto");
     * - `temperature` — температура генерации (от 0.0 до 2.0);
     * - `maxTokens` — максимальное количество токенов в ответе.
     *
     * @return [ChatResponseDto] с результатами генерации:
     * - `id` — уникальный идентификатор генерации;
     * - `model` — модель, использованная для ответа;
     * - `choices` — список вариантов ответов (объекты [ChoiceDto]);
     * - `usage` — информация об использовании токенов.
     */
    @POST("chat/completions")
    suspend fun postChatCompletions(
        @Body request: ChatRequestDto
    ): ChatResponseDto
}
