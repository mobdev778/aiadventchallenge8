package com.github.mobdev778.aiadventchallenge.data.chatclient.repository

import com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.model.ChoiceDto
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Choice
import org.koin.core.annotation.Single

/**
 * Маппер, преобразующий DTO-объект варианта завершения [ChoiceDto] в соответствующую доменную модель [Choice].
 *
 * Компонент зарегистрирован в контейнере Koin как синглтон. Делегирует преобразование вложенных сущностей
 * (сообщения и причины завершения) специализированным мапперам [MessageMapper] и [FinishReasonMapper],
 * обеспечивая модульность и соблюдение единственной ответственности.
 *
 * @property finishReasonMapper Маппер для преобразования причины завершения генерации.
 * @property messageMapper Маппер для преобразования сообщения.
 */
@Single
class ChoiceMapper(
    private val finishReasonMapper: FinishReasonMapper,
    private val messageMapper: MessageMapper,
) {

    /**
     * Выполняет преобразование DTO-объекта варианта ответа в доменную модель [Choice].
     *
     * @param choiceDto DTO-представление варианта ответа, полученное от API.
     * @return Доменная модель [Choice], построенная на основе переданного DTO.
     */
    fun map(choiceDto: ChoiceDto): Choice {
        return Choice(
            message = messageMapper.map(choiceDto.message),
            finishReason = finishReasonMapper.map(choiceDto.finishReason),
        )
    }
}
