package com.github.mobdev778.aiadventchallenge.domain.reasoningstrategy

import com.github.mobdev778.aiadventchallenge.data.settings.repository.SettingsRepository
import com.github.mobdev778.aiadventchallenge.domain.chatclient.ChatClient
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Message
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Role
import org.koin.core.annotation.Single

/**
 * Стратегия рассуждения, реализующая подход "Пошаговое решение (Chain of Thought)".
 *
 * При использовании данной стратегии модель получает системную инструкцию,
 * предписывающую решать задачу последовательно, описывая логику каждого шага.
 * Затем к сообщению добавляется опциональный системный промпт (например, контекст задачи)
 * и запрос пользователя. Итоговый [ChatRequest] отправляется через [ChatClient],
 * а из ответа извлекается первый вариант сгенерированного сообщения.
 *
 * Аннотирована [@Single][Single] (Koin) – во всём приложении существует один экземпляр этой стратегии.
 *
 * @property settingsRepository Репозиторий настроек, предоставляющий текущую модель ([AppSettings.baseModel]).
 * @property client Фасад чат-клиента для отправки запросов к языковой модели.
 */
@Single
class StepByStepStrategy(
    private val settingsRepository: SettingsRepository,
    private val client: ChatClient,
) : ReasoningStrategy {

    /**
     * Название стратегии, отображаемое в интерфейсе.
     */
    override val name: String = "Пошаговое решение (Chain of Thought)"

    /**
     * Отправляет запрос языковой модели с инструкцией пошагового решения.
     *
     * Формирует список сообщений: сначала идёт жёсткая системная инструкция,
     * затем опциональный дополнительный системный промпт [system] (если не `null`),
     * после чего – сообщение пользователя [user]. Итоговый запрос выполняется через [client]
     * с использованием модели, указанной в настройках ([SettingsRepository.getSettings().baseModel]),
     * и заданной температурой [temperature].
     *
     * @param system Дополнительный системный промпт, например, описание контекста или правил.
     *               Может быть `null`, если дополнительный контекст не требуется.
     * @param user Текст задачи или вопроса от пользователя.
     * @param temperature Температура генерации (степень случайности ответов).
     * @return Текстовое содержимое первого ответа модели либо константа [ReasoningStrategy.NO_ANSWER],
     *         если ответ не содержит сообщений или модель вернула пустой результат.
     */
    override suspend fun solve(
        system: String?,
        user: String,
        temperature: Double,
    ): String {
        val messages = mutableListOf<Message>()

        messages.add(
            Message(
                role = Role.System,
                content = "**Инструкция:** Решай задачу строго пошагово, расписывая логику каждого действия.",
            )
        )

        system?.let {
            messages.add(
                Message(role = Role.System, content = system)
            )
        }

        messages.add(
            Message(role = Role.User, content = user)
        )

        val response = client.execute(
            ChatRequest(
                model = settingsRepository.getSettings().baseModel,
                messages = messages,
                temperature = temperature,
            )
        )
        return response.choices.firstOrNull()?.message?.content ?: ReasoningStrategy.NO_ANSWER
    }
}
