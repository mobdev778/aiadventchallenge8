package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.model

import com.github.mobdev778.aiadventchallenge.domain.settings.model.ContextManagementType

/**
 * UI-модель, представляющая тип управления контекстом для отображения в пользовательском интерфейсе.
 *
 * @property type Тип управления контекстом из доменной модели.
 * @property label Локализованная или отображаемая метка для данного типа.
 * @property selected Флаг, указывающий, выбран ли данный тип в текущем интерфейсе.
 */
data class ContextManagementTypeUi(
    val type: ContextManagementType,
    val label: String,
    val selected: Boolean,
)

/**
 * Возвращает тип управления контекстом, соответствующий выбранному элементу списка.
 *
 * @return Первый [ContextManagementType], у которого свойство [selected] равно `true`.
 * @throws NoSuchElementException если ни один элемент не выбран (список не содержит элементов с `selected == true`).
 */
fun List<ContextManagementTypeUi>.getSelected(): ContextManagementType {
    return first { it.selected }.type
}
