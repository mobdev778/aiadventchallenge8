package com.github.mobdev778.aiadventchallenge.presentation.profilelistscreen

import java.util.UUID

/**
 * Запечатанный интерфейс, представляющий все возможные события,
 * которые могут произойти на экране списка профилей.
 *
 * Используется для организации однонаправленного потока данных (UDF):
 * UI-компоненты генерируют события, а ViewModel или другой обработчик
 * реагирует на них, изменяя состояние экрана.
 */
sealed interface ProfileListScreenEvent {
    /**
     * Событие нажатия на кнопку "Назад".
     * Обычно приводит к завершению текущего экрана и возврату на предыдущий.
     */
    data object OnBackClick : ProfileListScreenEvent

    /**
     * Событие нажатия на кнопку добавления нового профиля.
     * Инициирует переход на экран создания профиля.
     */
    data object OnAddProfileClick : ProfileListScreenEvent

    /**
     * Событие удаления профиля.
     *
     * @param profileId Уникальный идентификатор профиля, подлежащего удалению.
     */
    data class OnDeleteProfileClick(val profileId: UUID) : ProfileListScreenEvent

    /**
     * Событие выбора профиля из списка.
     * Обычно приводит к переходу на экран деталей выбранного профиля
     * или к активации профиля для дальнейшей работы.
     *
     * @param profileId Уникальный идентификатор выбранного профиля.
     */
    data class OnSelectProfile(val profileId: UUID) : ProfileListScreenEvent

    /**
     * Событие запроса на редактирование профиля.
     * Инициирует переход на экран редактирования указанного профиля.
     *
     * @param profileId Уникальный идентификатор профиля, который требуется отредактировать.
     */
    data class OnEditProfile(val profileId: UUID) : ProfileListScreenEvent
}
