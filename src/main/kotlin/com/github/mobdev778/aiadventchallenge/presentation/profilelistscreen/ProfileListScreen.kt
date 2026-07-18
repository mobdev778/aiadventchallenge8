package com.github.mobdev778.aiadventchallenge.presentation.profilelistscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.github.mobdev778.aiadventchallenge.presentation.profilelistscreen.composable.ProfileListScreenContent
import org.koin.java.KoinJavaComponent.inject
import java.util.UUID

/**
 * Основной экран со списком профилей.
 *
 * Этот Composable компонент отвечает за отображение списка профилей, используя [ProfileListScreenContent],
 * и за обработку команд, исходящих из UI, через [ProfileListScreenStateHolder].
 * Команды, такие как навигация назад, добавление или редактирование профиля,
 * преобразуются в вызовы переданных колбеков.
 *
 * @param onBack вызывается при необходимости вернуться на предыдущий экран.
 * @param onOpenAddProfile вызывается для открытия экрана добавления нового профиля.
 * @param onOpenEditProfile вызывается для открытия экрана редактирования профиля;
 *   принимает [UUID] идентификатор профиля.
 */
@Composable
fun ProfileListScreen(
    onBack: () -> Unit,
    onOpenAddProfile: () -> Unit,
    onOpenEditProfile: (UUID) -> Unit,
) {
    val stateHolder = remember {
        inject<ProfileListScreenStateHolder>(ProfileListScreenStateHolder::class.java).value
    }

    val profiles by stateHolder.profiles.collectAsState()

    ProfileListScreenContent(
        profiles = profiles,
        onEvent = stateHolder::onEvent,
    )

    LaunchedEffect(Unit) {
        stateHolder.commands.collect { command ->
            when (command) {
                ProfileListScreenCommand.Back -> onBack()
                ProfileListScreenCommand.OpenAddProfile -> onOpenAddProfile()
                is ProfileListScreenCommand.OpenEditProfile -> onOpenEditProfile(command.profileId)
            }
        }
    }
}
