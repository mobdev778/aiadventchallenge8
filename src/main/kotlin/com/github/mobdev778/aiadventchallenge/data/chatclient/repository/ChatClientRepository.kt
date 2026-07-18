package com.github.mobdev778.aiadventchallenge.data.chatclient.repository

import com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.ChatRestApi
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ChatRequest
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ChatResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import org.koin.java.KoinJavaComponent.inject
import retrofit2.Retrofit

/**
 * Репозиторий для отправки запросов к чат-моделям через REST API.
 *
 * Отвечает за сетевое взаимодействие с сервисом чат-комплишнов, скрывая детали работы с Retrofit и маппинга
 * данных. Выступает мостом между слоем domain (чистая бизнес-логика) и data (DTO и сетевые вызовы).
 *
 * Использует [ChatRequestMapper] для преобразования доменного запроса [ChatRequest] в DTO,
 * и [ChatResponseMapper] для обратного преобразования ответа из DTO в доменную модель [ChatResponse].
 * При каждом вызове динамически получает экземпляр [Retrofit] через Koin, что позволяет
 * гибко менять параметры подключения в рантайме.
 */
@Single
class ChatClientRepository(
    private val requestMapper: ChatRequestMapper,
    private val responseMapper: ChatResponseMapper,
) {

    /**
     * Отправляет запрос к модели чата и возвращает ответ.
     *
     * Выполняется на фоновом потоке ([Dispatchers.IO]), так как включает сетевую операцию.
     * Метод динамически получает экземпляр [Retrofit] через Koin, чтобы учесть возможные
     * изменения конфигурации, создаёт реализацию [ChatRestApi], маппит доменный [ChatRequest]
     * в DTO, отправляет его на сервер и преобразует ответ обратно в [ChatResponse].
     *
     * @param request доменная модель запроса, содержащая параметры генерации
     *   (модель, сообщения, температуру, инструменты и т.д.)
     * @return доменная модель ответа [ChatResponse]
     *   с вариантами сгенерированных сообщений и метаданными использования токенов
     */
    suspend fun sendRequest(
        request: ChatRequest,
    ): ChatResponse {
        return withContext(Dispatchers.IO) {
            // настройки доступа к модели могут меняться на ходу, поэтому мы каждый раз строим ретрофит заново
            val retrofit: Retrofit by inject(Retrofit::class.java)
            val restApi: ChatRestApi = retrofit.create(ChatRestApi::class.java)
            val requestDto = requestMapper.map(request)
            val responseDto = restApi.postChatCompletions(requestDto)
            responseMapper.map(responseDto)
        }
    }
}
