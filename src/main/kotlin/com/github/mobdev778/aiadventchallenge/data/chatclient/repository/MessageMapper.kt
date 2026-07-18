package com.github.mobdev778.aiadventchallenge.data.chatclient.repository

import com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.model.MessageDto
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Message
import org.koin.core.annotation.Single

/**
 * Маппер, отвечающий за двустороннее преобразование между доменной моделью [Message]
 * и её DTO-представлением [MessageDto].
 *
 * Изолирует слой данных от доменной логики, используя [RoleMapper] для конвертации ролей
 * и [ToolCallMapper] для преобразования вложенных вызовов инструментов.
 *
 * Благодаря аннотации [@Single] является синглтоном в контексте Koin и может переиспользоваться
 * во всём приложении.
 */
@Single
class MessageMapper(
    private val roleMapper: RoleMapper,
    private val toolCallMapper: ToolCallMapper,
) {

    /**
     * Преобразует доменное сообщение [Message] в объект передачи данных [MessageDto].
     *
     * @param message исходное сообщение доменного слоя.
     * @return экземпляр [MessageDto], готовый для отправки или сохранения,
     *         с корректно преобразованными ролями, вызовами инструментов и
     *         дополнительными MCP-параметрами (name, toolCallId).
     */
    fun map(message: Message): MessageDto {
        return MessageDto(
            role = roleMapper.map(message.role),
            content = message.content,
            // доп параметры MCP:
            name = message.name,
            toolCallId = message.toolCallId,
            toolCalls = message.toolCalls?.map {
                toolCallMapper.map(it)
            },
        )
    }

    /**
     * Преобразует DTO [MessageDto] обратно в доменную модель [Message].
     *
     * Используется при получении данных от AI-клиента или из хранилища для восстановления
     * бизнес-объекта.
     *
     * @param messageDto объект передачи данных.
     * @return экземпляр [Message], готовый к использованию в доменном слое, со всеми
     *         сопутствующими полями, включая MCP-атрибуты.
     */
    fun map(messageDto: MessageDto): Message {
        return Message(
            role = roleMapper.map(messageDto.role),
            content = messageDto.content,
            // доп.параметры MCP:
            name = messageDto.name,
            toolCallId = messageDto.toolCallId,
            toolCalls = messageDto.toolCalls?.map {
                toolCallMapper.map(it)
            },
        )
    }
}
