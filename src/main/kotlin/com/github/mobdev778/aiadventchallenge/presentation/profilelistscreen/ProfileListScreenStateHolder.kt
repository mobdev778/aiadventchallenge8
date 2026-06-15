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

@Single
class ProfileListScreenStateHolder(
    private val profileRepository: ProfileRepository,
    private val scope: CoroutineScope,
) {

    val profiles: StateFlow<List<Profile>> = profileRepository
        .observeProfiles()
        .flowOn(Dispatchers.IO)
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), emptyList())

    val commands = MutableSharedFlow<ProfileListScreenCommand>(
        // Так команда дождется, пока Compose-экран будет готов ее принять.
        extraBufferCapacity = 1,
    )

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
