package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.model

import com.github.mobdev778.aiadventchallenge.domain.settings.model.AppSettings

/**
 * Состояние экрана настроек приложения.
 *
 * Содержит текущие сохраненные настройки, редактируемый черновик, доступные типы управления контекстом
 * и флаг, разрешающий выполнение действия (например, кнопки «Сохранить»).
 *
 * @property saved Сохраненные в доменной модели настройки приложения ([AppSettings]).
 * @property draft Текущий редактируемый черновик настроек, который может быть изменен пользователем.
 * @property contextManagementTypes Список доступных вариантов управления контекстом для отображения в UI,
 * каждый представленный моделью [ContextManagementTypeUi].
 * @property actionEnabled Флаг, указывающий, доступно ли основное действие (например, кнопка «Сохранить»).
 */
data class SettingsScreenState(
    val saved: AppSettings,
    val draft: AppSettings,
    val contextManagementTypes: List<ContextManagementTypeUi>,
    val actionEnabled: Boolean,
)
