package com.github.mobdev778.aiadventchallenge.data.imageclient.repository

import com.github.mobdev778.aiadventchallenge.data.imageclient.datasource.ImageRestApi
import com.github.mobdev778.aiadventchallenge.domain.imageclient.model.ImageRequest
import com.github.mobdev778.aiadventchallenge.domain.imageclient.model.ImageResponse
import org.koin.core.annotation.Single
import org.koin.java.KoinJavaComponent.inject
import retrofit2.Retrofit

/**
 * Репозиторий для выполнения запросов на генерацию изображений к удалённому API.
 *
 * Обеспечивает связь между доменным слоем и сетевым слоем данных. Инкапсулирует логику
 * динамического создания экземпляра [ImageRestApi] через [Retrofit] с учётом возможного
 * изменения настроек доступа к модели на лету, а также преобразование доменных моделей
 * в DTO и обратно с использованием мапперов.
 *
 * @property requestMapper Маппер для преобразования доменного запроса [ImageRequest] в DTO [ImageRequestDto].
 * @property responseMapper Маппер для преобразования DTO ответа [ImageResponseDto] в доменный [ImageResponse].
 */
@Single
class ImageRepository(
    private val requestMapper: ImageRequestMapper,
    private val responseMapper: ImageResponseMapper,
) {

    /**
     * Выполняет запрос на генерацию изображений на основе переданного доменного объекта [ImageRequest].
     *
     * Из-за того, что настройки доступа к модели ИИ могут изменяться в процессе работы приложения,
     * экземпляр [Retrofit] и основанный на нём [ImageRestApi] создаются заново при каждом вызове.
     * Это гарантирует использование актуальных параметров подключения.
     *
     * @param request Доменная модель запроса с параметрами генерации: идентификатор модели, промпт, размер.
     * @return Доменная модель [ImageResponse], содержащая результат генерации (список изображений и временную метку).
     */
    suspend fun imagesGenerations(
        request: ImageRequest,
    ): ImageResponse {
        // настройки доступа к модели могут меняться на ходу, поэтому мы каждый раз строим ретрофит заново
        val retrofit: Retrofit by inject(Retrofit::class.java)
        val restApi: ImageRestApi = retrofit.create(ImageRestApi::class.java)
        val requestDto = requestMapper.map(request)
        val responseDto = restApi.imagesGenerations(requestDto)
        return responseMapper.map(responseDto)
    }
}
