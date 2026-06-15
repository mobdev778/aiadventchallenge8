package com.github.mobdev778.aiadventchallenge.presentation.editprofilescreen

import com.github.mobdev778.aiadventchallenge.data.profile.repository.ProfileRepository
import com.github.mobdev778.aiadventchallenge.domain.profile.model.Profile
import com.github.mobdev778.aiadventchallenge.presentation.editprofilescreen.model.EditProfileScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single
import java.util.UUID

@Single
class EditProfileScreenStateHolder(
    private val profileRepository: ProfileRepository,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(EditProfileScreenState())
    val state: StateFlow<EditProfileScreenState> = _state.asStateFlow()

    val commands = MutableSharedFlow<EditProfileScreenCommand>(
        // Так команда дождется, пока Compose-экран будет готов ее принять.
        extraBufferCapacity = 1,
    )

    fun onProfileId(profileId: UUID) {
        // Сохраняем id и подгружаем данные профиля.
        _state.update { it.copy(profileId = profileId) }

        scope.launch(Dispatchers.IO) {
            val profile = profileRepository.getProfile(profileId) ?: return@launch
            _state.update {
                it.copy(
                    name = profile.name,
                    content = profile.content,
                )
            }
        }
    }

    fun onEvent(event: EditProfileScreenEvent) {
        when (event) {
            EditProfileScreenEvent.OnBackClick -> commands.tryEmit(EditProfileScreenCommand.Back)
            is EditProfileScreenEvent.OnNameChange -> _state.update { it.copy(name = event.value) }
            is EditProfileScreenEvent.OnContentChange -> _state.update { it.copy(content = event.value) }
            EditProfileScreenEvent.OnEditClick -> editProfile()
        }
    }

    private fun editProfile() {
        val snapshot = state.value
        val profileId = snapshot.profileId ?: return

        // Защита от редактирования дефолтного профиля (на всякий случай).
        if (profileId == Profile.default.id) return

        scope.launch(Dispatchers.IO) {
            val updated = Profile(
                id = profileId,
                name = snapshot.name,
                content = snapshot.content,
                // selection не меняем при редактировании
                isSelected = profileRepository.getProfile(profileId)?.isSelected ?: false,
            )
            profileRepository.updateProfile(updated)
            commands.tryEmit(EditProfileScreenCommand.Back)
        }
    }
}
