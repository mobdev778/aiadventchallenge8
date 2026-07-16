package com.github.mobdev778.aiadventchallenge.data.chatclient.repository

import com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.model.ChatRequestDto
import com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.model.FunctionDto
import com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.model.ToolDto
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.ChatRequest
import io.modelcontextprotocol.kotlin.sdk.types.Tool
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.putJsonArray
import org.koin.core.annotation.Single

/**
 * Маппер, преобразующий доменный запрос [ChatRequest] в DTO [ChatRequestDto].
 *
 * Компонент отвечает за конвертацию всех полей запроса, включая
 * MCP-инструменты ([Tool]) и уровень усилий рассуждения, используя
 * соответствующие мапперы [ReasoningEffortMapper] и [MessageMapper].
 *
 * Зарегистрирован как синглтон в Koin для повторного использования
 * во всем приложении.
 *
 * @property reasoningEffortMapper маппер уровня усилий рассуждения
 * @property messageMapper маппер сообщений
 */
@Single
class ChatRequestMapper(
    private val reasoningEffortMapper: ReasoningEffortMapper,
    private val messageMapper: MessageMapper,
) {

    /**
     * Преобразует доменный запрос [ChatRequest] в DTO [ChatRequestDto].
     *
     * @param request исходный запрос доменного слоя, содержащий модель,
     *                историю сообщений, температуру и список инструментов.
     * @return готовый к отправке DTO с сериализованными данными,
     *         автоматическим выбором инструментов (если они есть) и
     *         преобразованными сообщениями.
     */
    fun map(request: ChatRequest): ChatRequestDto {
        val tools = request.tools
            .takeIf { it.isNotEmpty() }
            ?.map(::map)

        return ChatRequestDto(
            model = request.model,
            reasoningEffort = request.reasoningEffort?.let {
                reasoningEffortMapper.map(it)
            },
            messages = request.messages.map { message ->
                messageMapper.map(message)
            },
            tools = tools,
            toolChoice = tools?.let { "auto" },
            temperature = request.temperature
        )
    }

    /**
     * Преобразует MCP-инструмент [Tool] в DTO-представление [ToolDto].
     *
     * На основе [Tool.inputSchema] строится JSON-объект параметров,
     * включающий свойства и список обязательных полей.
     *
     * @param tool MCP-инструмент, предоставляемый контекстом или сервером MCP.
     * @return DTO инструмента с типом "function" и спецификацией вызываемой функции.
     */
    private fun map(tool: Tool): ToolDto {
        val parametersJson = buildJsonObject {
            put("type", JsonPrimitive("object"))

            val props = tool.inputSchema.properties
            if (props != null) {
                put("properties", props)
            }

            val requiredList = tool.inputSchema.required
            if (requiredList?.isNotEmpty() == true) {
                putJsonArray("required") {
                    requiredList.forEach { add(JsonPrimitive(it)) }
                }
            }
        }

        return ToolDto(
            type = "function",
            function = FunctionDto(
                name = tool.name,
                description = tool.description,
                parameters = parametersJson,
            )
        )
    }
}
