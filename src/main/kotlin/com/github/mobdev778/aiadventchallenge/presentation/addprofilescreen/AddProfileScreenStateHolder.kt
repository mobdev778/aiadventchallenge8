package com.github.mobdev778.aiadventchallenge.presentation.addprofilescreen

import com.github.mobdev778.aiadventchallenge.data.profile.repository.ProfileRepository
import com.github.mobdev778.aiadventchallenge.domain.profile.model.Profile
import com.github.mobdev778.aiadventchallenge.presentation.addprofilescreen.model.AddProfileScreenState
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
class AddProfileScreenStateHolder(
    private val profileRepository: ProfileRepository,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(AddProfileScreenState())
    val state: StateFlow<AddProfileScreenState> = _state.asStateFlow()

    val commands = MutableSharedFlow<AddProfileScreenCommand>(
        // Так команда дождется, пока Compose-экран будет готов ее принять.
        extraBufferCapacity = 1,
    )

    fun onEvent(event: AddProfileScreenEvent) {
        when (event) {
            AddProfileScreenEvent.OnBackClick -> commands.tryEmit(AddProfileScreenCommand.Back)
            is AddProfileScreenEvent.OnNameChange -> _state.update { it.copy(name = event.value) }
            is AddProfileScreenEvent.OnContentChange -> _state.update { it.copy(content = event.value) }
            AddProfileScreenEvent.OnAddClick -> addProfile()
        }
    }

    private fun addProfile() {
        val snapshot = state.value
        scope.launch(Dispatchers.IO) {
            val profile = Profile(
                id = UUID.randomUUID(),
                name = snapshot.name,
                content = snapshot.content,
                isSelected = false,
            )
            profileRepository.createProfile(profile)
            commands.tryEmit(AddProfileScreenCommand.Back)
        }
    }
}
