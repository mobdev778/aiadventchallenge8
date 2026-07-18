package com.github.mobdev778.aiadventchallenge.domain.mymcpserver.rag

import com.github.mobdev778.aiadventchallenge.data.settings.repository.SettingsRepository
import com.github.mobdev778.aiadventchallenge.domain.chatclient.ChatClient
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Message
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Role
import org.koin.core.annotation.Single

/**
 * Фильтр, определяющий наличие русских букв в запросе и при необходимости выполняющий
 * перевод текста на английский язык через языковую модель.
 *
 * Компонент используется в контексте RAG-системы (Retrieval-Augmented Generation) для
 * нормализации запросов пользователя. Если входной текст содержит символы кириллицы,
 * он переводится на английский с помощью текущей модели, указанной в [настройках][AppSettings].
 *
 * @property settingsRepository Репозиторий для доступа к текущим настройкам приложения.
 *   Через него извлекается идентификатор модели (baseModel) для выполнения перевода.
 * @property chatClient Клиент взаимодействия с языковой моделью, предоставляющий метод
 *   [ChatClient.execute] для отправки запроса на перевод.
 */
@Single
class RagRussianFilter(
    private val settingsRepository: SettingsRepository,
    private val chatClient: ChatClient,
) {

    /**
     * Обрабатывает исходный запрос: если он содержит русские буквы, выполняет перевод
     * на английский язык с помощью текущей языковой модели; иначе возвращает запрос без изменений.
     *
     * Алгоритм:
     * 1. Проверяет наличие кириллических символов в строке [query] с помощью [hasRussianLetters].
     * 2. Если русских букв нет, сразу возвращает исходную строку.
     * 3. Если есть – получает текущую модель из настроек через [settingsRepository.getSettings]
     *    и формирует запрос [ChatRequest] к модели с инструкцией перевести текст пользователя
     *    на английский и вернуть только перевод.
     * 4. Отправляет запрос через [chatClient.execute] и извлекает содержимое первого варианта ответа.
     * 5. Если ответ не содержит сообщения (например, при ошибке), возвращает оригинальный запрос
     *    в качестве fallback-стратегии.
     *
     * @param query Исходный запрос, потенциально содержащий русский текст.
     * @return Строка запроса на английском языке (если был выполнен перевод) или оригинальный запрос,
     *         если перевод не потребовался или не удался.
     */
    suspend fun filter(query: String): String {
        return if (hasRussianLetters(query)) {
            val settings = settingsRepository.getSettings()
            val response = chatClient.execute(
                request = ChatRequest(
                    model = settings.baseModel,
                    messages = listOf(
                        Message(
                            role = Role.User,
                            content = "Переведи текст из запроса пользователя на английский. " +
                                "Верни только перевод, без комментариев и дополнительных рассуждений. " +
                                "Вот текст: $query",
                        ),
                    )
                )
            )
            response.choices.firstOrNull()?.message?.content ?: query
        } else {
            query
        }
    }

    /**
     * Множество всех русских букв (заглавных и строчных) для быстрой проверки наличия кириллицы.
     */
    private val russianLetters = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя".toSet()

    /**
     * Проверяет, содержит ли строка хотя бы одну русскую букву.
     *
     * @param query Строка для анализа.
     * @return `true`, если найден хотя бы один символ из [RUSSIAN_LETTERS]; иначе `false`.
     */
    private fun hasRussianLetters(query: String): Boolean {
        for (c in query) {
            if (russianLetters.contains(c)) {
                return true
            }
        }
        return false
    }
}
