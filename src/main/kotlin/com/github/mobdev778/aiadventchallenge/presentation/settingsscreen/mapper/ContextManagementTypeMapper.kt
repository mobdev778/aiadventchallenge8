package com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.mapper

import com.github.mobdev778.aiadventchallenge.domain.settings.model.ContextManagementType
import com.github.mobdev778.aiadventchallenge.presentation.settingsscreen.model.ContextManagementTypeUi

/**
 * Маппер для преобразования [ContextManagementType] в UI-модель [ContextManagementTypeUi].
 *
 * Отвечает за преобразование доменной сущности, описывающей тип управления контекстом,
 * в представление, готовое к отображению в пользовательском интерфейсе. Назначает
 * человекочитаемые метки для каждого типа и передаёт флаг выбранного состояния.
 * Является связующим звеном между доменным слоем и слоем представления в рамках работы
 * с настройками контекста.
 */
object ContextManagementTypeMapper {

    /**
     * Преобразует заданный [ContextManagementType] в объект [ContextManagementTypeUi] с
     * соответствующим текстовым описанием и флагом выбора.
     *
     * @param contextManagementType Тип управления контекстом из доменной модели, который
     *        необходимо отобразить в UI.
     * @param selected Флаг, показывающий, выбран ли данный тип в текущем интерфейсе.
     * @return Готовая к отображению UI-модель [ContextManagementTypeUi] с меткой и признаком
     *         выбора.
     */
    fun map(contextManagementType: ContextManagementType, selected: Boolean): ContextManagementTypeUi {
        return ContextManagementTypeUi(
            type = contextManagementType,
            label = when (contextManagementType) {
                ContextManagementType.None -> "- Не используется -"
                ContextManagementType.SlidingWindow -> "Sliding Window"
                ContextManagementType.StickyFacts -> "Sticky Facts"
                ContextManagementType.Branching -> "Branching"
            },
            selected = selected,
        )
    }
}
