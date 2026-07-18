package com.github.mobdev778.aiadventchallenge.domain.chatclient

import com.github.mobdev778.aiadventchallenge.data.chatclient.repository.ChatClientRepository
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ChatResponse
import org.koin.core.annotation.Single

/**
 * Фасад чат-клиента в доменном слое.
 *
 * Данный класс инкапсулирует логику взаимодействия с Chat API и предоставляет единственную точку
 * отправки запроса. Он делегирует фактическую отправку репозиторию [ChatClientRepository],
 * оставляя за собой только оркестрацию потока данных.
 *
 * Аннотирован [@Single][Single] (Koin), поэтому во всём приложении будет существовать
 * один экземпляр этого класса.
 *
 * @property repository Репозиторий, отвечающий за низкоуровневую отправку запроса к Chat API.
 * @see ChatRequest
 * @see ChatResponse
 */
@Single
class ChatClient(
    private val repository: ChatClientRepository,
) {

    /**
     * Выполняет запрос к языковой модели и возвращает ответ.
     *
     * Функция принимает полностью сформированный объект [ChatRequest], включающий модель,
     * историю сообщений, температуру и другие параметры (см. [ChatRequest]),
     * и транслирует его в репозиторий. Результатом является [ChatResponse], который содержит
     * список вариантов ответа
     * ([Choice][com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Choice])
     * и метаданные об использованных токенах
     * ([Usage][com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Usage]).
     *
     * @param request Запрос к языковой модели, подготовленный в соответствии с требованиями API.
     * @return Ответ от Chat API, включающий сгенерированные сообщения и информацию о затраченных токенах.
     * @throws Throwable При ошибках сетевого взаимодействия или некорректном ответе сервера.
     */
    suspend fun execute(request: ChatRequest): ChatResponse {
        return repository.sendRequest(request)
    }
}
