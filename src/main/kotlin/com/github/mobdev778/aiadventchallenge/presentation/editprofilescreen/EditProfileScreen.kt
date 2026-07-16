package com.github.mobdev778.aiadventchallenge.presentation.editprofilescreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.github.mobdev778.aiadventchallenge.presentation.editprofilescreen.composable.EditProfileScreenContent
import org.koin.java.KoinJavaComponent.inject
import java.util.UUID

/**
 * Composable-функция, представляющая экран редактирования профиля.
 *
 * Отвечает за связь UI с бизнес-логикой через [EditProfileScreenStateHolder],
 * обработку пользовательских событий и навигацию.
 * Подписывается на команды ([EditProfileScreenCommand]) от state holder'а,
 * чтобы выполнить обратный вызов [onBack] при получении команды [EditProfileScreenCommand.Back].
 *
 * @param profileId Уникальный идентификатор профиля, для которого открывается экран.
 * @param onBack Колбэк, вызываемый при необходимости вернуться на предыдущий экран.
 */
@Composable
fun EditProfileScreen(
    profileId: UUID,
    onBack: () -> Unit,
) {
    val stateHolder = remember {
        inject<EditProfileScreenStateHolder>(EditProfileScreenStateHolder::class.java).value
    }

    val state by stateHolder.state.collectAsState()

    EditProfileScreenContent(
        state = state,
        onEvent = stateHolder::onEvent,
    )

    LaunchedEffect(profileId) {
        stateHolder.onProfileId(profileId)
    }

    LaunchedEffect(Unit) {
        stateHolder.commands.collect { command ->
            when (command) {
                EditProfileScreenCommand.Back -> onBack()
            }
        }
    }
}
