package com.github.mobdev778.aiadventchallenge.domain.settings.model

/**
 * Настройки приложения, определяющие поведение и параметры взаимодействия с AI.
 *
 * Включает в себя тип управления контекстом ([ContextManagementType]), ограничения на количество сообщений
 * и токенов, параметры для рекурсивного суммирования и закрепленных фактов, а также данные для подключения
 * к API (ключ, базовый URL, модель).
 *
 * @property contextManagementType Тип управления контекстом, влияющий на то, как приложение хранит и обрабатывает
 * историю сообщений.
 * @property maxMessages Максимальное количество сообщений, используемых в контексте.
 * @property maxTokens Максимальное количество токенов, разрешенное для модели.
 * @property recursiveSummationMaxMessages Максимальное количество сообщений для рекурсивного суммирования
 * (используется при обработке длинных диалогов).
 * @property stickyFactsMaxMessages Максимальное количество сообщений, в которых сохраняются "закрепленные" факты
 * (ключевая информация, не теряющаяся при очистке контекста).
 * @property apiKey API-ключ для доступа к сервису AI.
 * @property baseUrl Базовый URL API (endpoint).
 * @property baseModel Имя модели AI по умолчанию.
 */
data class AppSettings(
    val contextManagementType: ContextManagementType,
    val maxMessages: Int,
    val maxTokens: Int,
    val recursiveSummationMaxMessages: Int,
    val stickyFactsMaxMessages: Int,
    val apiKey: String,
    val baseUrl: String,
    val baseModel: String,
)
