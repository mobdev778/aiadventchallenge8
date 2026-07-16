package com.github.mobdev778.aiadventchallenge.presentation.profilelistscreen

import com.github.mobdev778.aiadventchallenge.data.profile.repository.ProfileRepository
import com.github.mobdev778.aiadventchallenge.domain.profile.model.Profile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import java.util.UUID

private const val STATE_FLOW_TIMEOUT_MS = 5000L

/**
 * Компонент-держатель состояния экрана списка профилей.
 *
 * Отвечает за предоставление UI-слою актуального списка профилей через [profiles],
 * за обработку событий экрана (см. [ProfileListScreenEvent]) и формирование
 * команд навигации/действий через [commands].
 *
 * Является синглтоном в DI-контейнере Koin и оперирует корутинами в рамках
 * переданного [scope] (обычно с привязкой к жизненному циклу экрана).
 *
 * @param profileRepository репозиторий для работы с профилями.
 * @param scope корутин-скоуп, в котором выполняются фоновые операции.
 */
@Single
class ProfileListScreenStateHolder(
    private val profileRepository: ProfileRepository,
    private val scope: CoroutineScope,
) {

    /**
     * StateFlow, предоставляющий актуальный список профилей.
     *
     * Источником данных является [ProfileRepository.observeProfiles], обновления происходят в
     * фоновом потоке ([Dispatchers.IO]). Подписка активна пока есть хотя бы один подписчик,
     * но не более [STATE_FLOW_TIMEOUT_MS] миллисекунд после ухода последнего подписчика.
     */
    val profiles: StateFlow<List<Profile>> = profileRepository
        .observeProfiles()
        .flowOn(Dispatchers.IO)
        .stateIn(scope, SharingStarted.WhileSubscribed(STATE_FLOW_TIMEOUT_MS), emptyList())

    /**
     * SharedFlow для команд, адресованных UI (навигация, открытие экранов).
     *
     * Буфер ёмкостью 1 гарантирует, что последняя отправленная команда не будет потеряна,
     * даже если подписчик временно не готов к приёму (например, Compose-экран ещё не подписан).
     */
    val commands = MutableSharedFlow<ProfileListScreenCommand>(
        extraBufferCapacity = 1,
    )

    /**
     * Основной метод обработки событий, поступающих от UI экрана списка профилей.
     *
     * В зависимости от типа события выполняет соответствующие действия:
     * - [ProfileListScreenEvent.OnBackClick] – эмитит команду [ProfileListScreenCommand.Back];
     * - [ProfileListScreenEvent.OnAddProfileClick] – эмитит команду [ProfileListScreenCommand.OpenAddProfile];
     * - [ProfileListScreenEvent.OnDeleteProfileClick] – запускает удаление профиля через [deleteProfile];
     * - [ProfileListScreenEvent.OnSelectProfile] – запускает выбор профиля через [selectProfile];
     * - [ProfileListScreenEvent.OnEditProfile] – если профиль не является дефолтным ([Profile.default]),
     *   эмитит команду [ProfileListScreenCommand.OpenEditProfile].
     *
     * @param event событие, которое необходимо обработать.
     */
    fun onEvent(event: ProfileListScreenEvent) {
        when (event) {
            ProfileListScreenEvent.OnBackClick -> commands.tryEmit(ProfileListScreenCommand.Back)
            ProfileListScreenEvent.OnAddProfileClick -> commands.tryEmit(ProfileListScreenCommand.OpenAddProfile)
            is ProfileListScreenEvent.OnDeleteProfileClick -> deleteProfile(event.profileId)
            is ProfileListScreenEvent.OnSelectProfile -> {
                selectProfile(event.profileId)
            }
            is ProfileListScreenEvent.OnEditProfile -> {
                if (event.profileId != Profile.default.id) {
                    commands.tryEmit(ProfileListScreenCommand.OpenEditProfile(event.profileId))
                }
            }
        }
    }

    private fun deleteProfile(profileId: UUID) {
        // Защита от удаления дефолтного профиля.
        if (profileId == Profile.default.id) return

        scope.launch(Dispatchers.IO) {
            profileRepository.deleteProfile(profileId)
        }
    }

    private fun selectProfile(profileId: UUID) {
        scope.launch(Dispatchers.IO) {
            profileRepository.selectProfile(profileId)
        }
    }
}
