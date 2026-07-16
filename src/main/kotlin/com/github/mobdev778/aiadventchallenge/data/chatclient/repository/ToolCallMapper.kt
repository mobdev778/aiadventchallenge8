package com.github.mobdev778.aiadventchallenge.data.chatclient.repository

import com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.model.ToolCallDto
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ToolCall
import org.koin.core.annotation.Single

/**
 * Двусторонний маппер между доменной моделью [ToolCall] и объектом передачи данных [ToolCallDto].
 *
 * Изолирует слои приложения: доменный слой оперирует собственными типами, не зависящими от формата
 * взаимодействия с AI-клиентом. Для преобразования вложенного вызова функции использует
 * [FunctionCallMapper].
 *
 * Благодаря аннотации [@Single] является синглтоном в контексте Koin и может переиспользоваться
 * во всём приложении.
 */
@Single
class ToolCallMapper(
    private val functionCallMapper: FunctionCallMapper,
) {

    /**
     * Преобразует доменную модель [ToolCall] в DTO для отправки или сохранения.
     *
     * @param toolCall Исходный объект вызова инструмента, содержащий идентификатор, тип и детали функции.
     * @return Новый экземпляр [ToolCallDto] с данными, скопированными из [toolCall].
     */
    fun map(toolCall: ToolCall): ToolCallDto {
        return ToolCallDto(
            id = toolCall.id,
            type = toolCall.type,
            function = functionCallMapper.map(toolCall.function),
        )
    }

    /**
     * Преобразует DTO, полученный от AI-клиента, обратно в доменную модель [ToolCall].
     *
     * @param dto Объект передачи данных, содержащий идентификатор, тип и детали функции.
     * @return Новый экземпляр [ToolCall], готовый к использованию в доменном слое.
     */
    fun map(dto: ToolCallDto): ToolCall {
        return ToolCall(
            id = dto.id,
            type = dto.type,
            function = functionCallMapper.map(dto.function),
        )
    }
}
