package com.github.mobdev778.aiadventchallenge.data.chatclient.repository

import com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.model.FinishReasonDto
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.FinishReason
import org.koin.core.annotation.Single

/**
 * Маппер, преобразующий DTO-перечисление причин завершения генерации [FinishReasonDto]
 * в соответствующую доменную модель [FinishReason].
 *
 * Компонент зарегистрирован в контейнере Koin как синглтон.
 */
@Single
class FinishReasonMapper {

    /**
     * Выполняет преобразование DTO-объекта причины завершения в доменную модель.
     *
     * @param reason DTO-представление причины завершения, полученное от API.
     * @return Доменная модель [FinishReason], соответствующая переданному DTO.
     */
    fun map(reason: FinishReasonDto): FinishReason {
        return when (reason) {
            FinishReasonDto.Stop -> FinishReason.Stop
            FinishReasonDto.Length -> FinishReason.Length
            FinishReasonDto.ToolCalls -> FinishReason.ToolCalls
            FinishReasonDto.ContentFilter -> FinishReason.ContentFilter
            FinishReasonDto.RleMaxTokens -> FinishReason.RleMaxTokens
        }
    }
}
