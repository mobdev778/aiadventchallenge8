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

/**
 * Держатель состояния экрана добавления профиля, реализующий паттерн MVI.
 *
 * Управляет [AddProfileScreenState] – текущим состоянием формы, обрабатывает
 * пользовательские события [AddProfileScreenEvent] и посылает команды навигации
 * [AddProfileScreenCommand] через [commands].
 *
 * При событии [AddProfileScreenEvent.OnAddClick] создаёт новый профиль с помощью
 * [ProfileRepository], после чего отправляет команду [AddProfileScreenCommand.Back]
 * для возврата на предыдущий экран.
 *
 * @property profileRepository Репозиторий для работы с профилями.
 * @property scope CoroutineScope, в котором выполняются асинхронные операции (например, запись в базу данных).
 */
@Single
class AddProfileScreenStateHolder(
    private val profileRepository: ProfileRepository,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(AddProfileScreenState())

    /**
     * Неизменяемый поток состояния экрана, предоставляемый для подписки UI.
     */
    val state: StateFlow<AddProfileScreenState> = _state.asStateFlow()

    /**
     * Поток одноразовых команд навигации (например, возврат на предыдущий экран),
     * потребляемый Compose-экраном.
     *
     * Буфер ёмкостью 1 гарантирует, что команда дождётся готовности экрана её обработать.
     */
    val commands = MutableSharedFlow<AddProfileScreenCommand>(
        extraBufferCapacity = 1,
    )

    /**
     * Обрабатывает входящее событие от UI, изменяя состояние или инициируя побочные эффекты.
     *
     * @param event Событие экрана добавления профиля.
     */
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
