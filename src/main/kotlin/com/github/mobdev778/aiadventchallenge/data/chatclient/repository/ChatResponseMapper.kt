package com.github.mobdev778.aiadventchallenge.data.chatclient.repository

import com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.model.ChatResponseDto
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ChatResponse
import org.koin.core.annotation.Single

/**
 * Маппер, преобразующий DTO-объект ответа чата [ChatResponseDto] в соответствующую доменную модель [ChatResponse].
 *
 * Координирует преобразование вложенных сущностей: списка вариантов ответа ([Choice])
 * и данных об использовании токенов ([Usage])
 * с помощью специализированных мапперов [ChoiceMapper] и [UsageMapper].
 * Это способствует соблюдению принципа единственной ответственности
 * и облегчает тестирование.
 *
 * @property choiceMapper Маппер для преобразования вариантов ответа.
 * @property usageMapper Маппер для преобразования информации об использовании токенов.
 */
@Single
class ChatResponseMapper(
    private val usageMapper: UsageMapper,
    private val choiceMapper: ChoiceMapper,
) {

    /**
     * Преобразует DTO-представление ответа чата в доменную модель [ChatResponse].
     *
     * @param dto Исходный объект [ChatResponseDto], полученный от источника данных.
     * @return Доменная модель [ChatResponse], содержащая преобразованные варианты ответа
     *   и метаданные использования токенов.
     */
    fun map(dto: ChatResponseDto): ChatResponse {
        return ChatResponse(
            choices = dto.choices.map { choiceDto ->
                choiceMapper.map(choiceDto)
            },
            usage = dto.usage?.let {
                usageMapper.map(it)
            },
        )
    }
}
