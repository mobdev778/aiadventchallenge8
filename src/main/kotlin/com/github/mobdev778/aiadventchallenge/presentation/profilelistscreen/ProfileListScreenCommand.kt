package com.github.mobdev778.aiadventchallenge.presentation.profilelistscreen

import java.util.UUID

/**
 * Запечатанный интерфейс, описывающий все возможные команды, которые могут быть отправлены
 * из UI экрана списка профилей ([ProfileListScreen]) в ViewModel или другой управляющий компонент.
 * Используется для обработки намерений пользователя: навигация назад, добавление нового профиля
 * или открытие редактирования существующего.
 */
sealed interface ProfileListScreenCommand {
    /**
     * Команда навигации назад (возврат на предыдущий экран).
     */
    data object Back : ProfileListScreenCommand

    /**
     * Команда открытия экрана/диалога добавления нового профиля.
     */
    data object OpenAddProfile : ProfileListScreenCommand

    /**
     * Команда открытия экрана редактирования существующего профиля.
     *
     * @param profileId уникальный идентификатор профиля, подлежащего редактированию.
     */
    data class OpenEditProfile(val profileId: UUID) : ProfileListScreenCommand
}
