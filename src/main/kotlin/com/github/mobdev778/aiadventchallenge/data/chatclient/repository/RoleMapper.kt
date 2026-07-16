package com.github.mobdev778.aiadventchallenge.data.chatclient.repository

import com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.model.RoleDto
import com.github.mobdev778.aiadventchallenge.domain.chatclient.model.Role
import org.koin.core.annotation.Single

/**
 * Маппер, отвечающий за преобразование между доменной моделью [Role] и её DTO-представлением [RoleDto].
 *
 * Используется для преобразования объектов при передаче между слоями данных (datasource)
 * и доменным слоем, обеспечивая изоляцию от конкретных форматов сериализации.
 */
@Single
class RoleMapper {

    /**
     * Преобразует DTO-представление роли в доменную модель.
     *
     * @param dto объект [RoleDto], полученный из источника данных (например, из сетевого ответа).
     * @return соответствующее значение [Role].
     */
    fun map(dto: RoleDto): Role {
        return when (dto) {
            RoleDto.System -> Role.System
            RoleDto.User -> Role.User
            RoleDto.Assistant -> Role.Assistant
            RoleDto.Developer -> Role.Developer
            RoleDto.Tool -> Role.Tool
        }
    }

    /**
     * Преобразует доменную модель роли в DTO-представление.
     *
     * @param role значение [Role], используемое в бизнес-логике.
     * @return соответствующий объект [RoleDto], готовый к передаче во внешние слои.
     */
    fun map(role: Role): RoleDto {
        return when (role) {
            Role.System -> RoleDto.System
            Role.User -> RoleDto.User
            Role.Assistant -> RoleDto.Assistant
            Role.Developer -> RoleDto.Developer
            Role.Tool -> RoleDto.Tool
        }
    }
}
