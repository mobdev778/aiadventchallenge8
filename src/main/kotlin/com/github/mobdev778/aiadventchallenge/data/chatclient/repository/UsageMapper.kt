package com.github.mobdev778.aiadventchallenge.data.chatclient.repository

import com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.model.UsageDto
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Usage
import org.koin.core.annotation.Single

/**
 * Маппер, отвечающий за преобразование данных об использовании токенов
 * из слоя данных ([UsageDto]) в доменную модель ([Usage]).
 *
 * Изолирует детали DTO от бизнес-логики приложения. Аннотация @Single гарантирует,
 * что в графе зависимостей Koin будет существовать единственный экземпляр этого класса.
 */
@Single
class UsageMapper {

    /**
     * Преобразует DTO использования токенов в соответствующий объект доменной модели.
     *
     * @param dto Исходный объект [UsageDto], полученный от источника данных.
     * @return Экземпляр [Usage] с идентичным набором значений полей.
     */
    fun map(dto: UsageDto): Usage {
        return Usage(
            promptTokens = dto.promptTokens,
            completionTokens = dto.completionTokens,
            totalTokens = dto.totalTokens,
        )
    }
}
