package com.github.mobdev778.aiadventchallenge.data.chatclient.repository

import com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.model.FunctionCallDto
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.FunctionCall
import org.koin.core.annotation.Single

/**
 * Маппер, отвечающий за двустороннее преобразование между доменной моделью [FunctionCall]
 * и объектом передачи данных [FunctionCallDto].
 *
 * Используется для изоляции слоёв: доменная модель не зависит от формата обмена с AI-клиентом,
 * а DTO не содержит бизнес-логики. Благодаря аннотации [@Single] является синглтоном
 * в контексте внедрения зависимостей Koin и может быть переиспользован во всём приложении.
 */
@Single
class FunctionCallMapper {

    /**
     * Преобразует доменную модель [FunctionCall] в DTO для отправки или сохранения.
     *
     * @param functionCall Исходный объект вызова функции, содержащий имя и JSON-строку аргументов.
     * @return Новый экземпляр [FunctionCallDto] с данными, скопированными из [functionCall].
     */
    fun map(functionCall: FunctionCall): FunctionCallDto {
        return FunctionCallDto(
            name = functionCall.name,
            arguments = functionCall.arguments,
        )
    }

    /**
     * Преобразует полученный от AI-клиента DTO в доменную модель [FunctionCall].
     *
     * @param dto Объект передачи данных, содержащий имя функции и её аргументы в виде JSON-строки.
     * @return Новый экземпляр [FunctionCall], готовый к использованию в доменном слое.
     */
    fun map(dto: FunctionCallDto): FunctionCall {
        return FunctionCall(
            name = dto.name,
            arguments = dto.arguments,
        )
    }
}
