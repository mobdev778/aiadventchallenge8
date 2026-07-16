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

/**
 * Компонент управления состоянием экрана редактирования профиля, реализующий
 * паттерн UDF (Unidirectional Data Flow). Является центральным координатором
 * между слоем UI и доменным слоем (репозиторием профилей).
 *
 * Предоставляет:
 * - [state] — неизменяемый поток состояния [EditProfileScreenState] для подписки UI;
 * - [commands] — одноразовый канал команд навигации (например, [EditProfileScreenCommand.Back]),
 *   буферизирующий одно значение, чтобы команда дожидалась готовности экрана в Compose.
 *
 * Для получения данных используется [ProfileRepository], асинхронные операции
 * выполняются в переданном [scope] (обычно viewModelScope).
 * Помечена аннотацией [@Single][org.koin.core.annotation.Single] для использования в Koin-контейнере.
 */
@Single
class EditProfileScreenStateHolder(
    private val profileRepository: ProfileRepository,
    private val scope: CoroutineScope,
) {
    private val _state = MutableStateFlow(EditProfileScreenState())

    /** Текущее состояние экрана редактирования профиля, доступное только для чтения. */
    val state: StateFlow<EditProfileScreenState> = _state.asStateFlow()

    /**
     * Канал одноразовых команд навигации, буферизирующий одно значение.
     * Используется для передачи команд, таких как возврат на предыдущий экран,
     * в момент, когда UI готов их принять.
     */
    val commands = MutableSharedFlow<EditProfileScreenCommand>(
        // Так команда дождется, пока Compose-экран будет готов ее принять.
        extraBufferCapacity = 1,
    )

    /**
     * Сохраняет переданный идентификатор профиля и запускает асинхронную загрузку
     * данных профиля из [ProfileRepository]. После получения обновляет соответствующие
     * поля состояния [EditProfileScreenState.name] и [EditProfileScreenState.content].
     *
     * @param profileId Уникальный идентификатор профиля, данные которого необходимо загрузить.
     */
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

    /**
     * Принимает пользовательское событие с UI и в зависимости от его типа
     * либо обновляет состояние (редактирование полей), либо отправляет
     * команду в [commands], либо запускает сохранение профиля через [editProfile].
     *
     * @param event Событие экрана редактирования профиля.
     */
    fun onEvent(event: EditProfileScreenEvent) {
        when (event) {
            EditProfileScreenEvent.OnBackClick -> commands.tryEmit(EditProfileScreenCommand.Back)
            is EditProfileScreenEvent.OnNameChange -> _state.update { it.copy(name = event.value) }
            is EditProfileScreenEvent.OnContentChange -> _state.update { it.copy(content = event.value) }
            EditProfileScreenEvent.OnEditClick -> editProfile()
        }
    }

    /**
     * Выполняет сохранение отредактированного профиля.
     * Формирует объект [Profile] на основе текущего состояния, исключая изменение
     * статуса `isSelected`, и передаёт его в [ProfileRepository.updateProfile].
     * По завершении отправляет команду [EditProfileScreenCommand.Back] для возврата на предыдущий экран.
     * Если профиль совпадает с [Profile.default], сохранение игнорируется.
     */
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
