package com.github.mobdev778.aiadventchallenge.presentation.editprofilescreen

/**
 * Запечатанный интерфейс, представляющий все возможные пользовательские события
 * на экране редактирования профиля.
 *
 * Используется в архитектурном паттерне MVI (Model-View-Intent) или MVVM
 * для однозначной передачи действий пользователя от UI-слоя к ViewModel,
 * что обеспечивает строгую типизацию и полноту обработки всех событий
 * в выражении [when].
 */
sealed interface EditProfileScreenEvent {

    /**
     * Событие нажатия на кнопку "Назад".
     * Инициирует возврат на предыдущий экран без сохранения изменений.
     */
    data object OnBackClick : EditProfileScreenEvent

    /**
     * Событие изменения текста в поле имени профиля.
     *
     * @param value Новое значение, введённое пользователем в поле имени.
     */
    data class OnNameChange(val value: String) : EditProfileScreenEvent

    /**
     * Событие изменения текста в поле описания (контента) профиля.
     *
     * @param value Новое значение, введённое пользователем в поле контента.
     */
    data class OnContentChange(val value: String) : EditProfileScreenEvent

    /**
     * Событие нажатия на кнопку "Редактировать" (сохранить изменения).
     * Инициирует процесс сохранения отредактированных данных профиля.
     */
    data object OnEditClick : EditProfileScreenEvent
}
