package com.github.mobdev778.aiadventchallenge.presentation.rag.ragconfigscreen

import com.github.mobdev778.aiadventchallenge.domain.rag.model.RagFilterType

/**
 * Запечатанный интерфейс, описывающий все возможные пользовательские события
 * на экране конфигурации RAG (Retrieval-Augmented Generation).
 *
 * Каждое событие соответствует конкретному действию пользователя в UI
 * и используется для обновления состояния экрана через паттерн
 * "событие → изменение модели".
 *
 * @see RagFilterType
 */
sealed interface RagConfigScreenEvent {

    /**
     * Пользователь нажал на кнопку «Назад».
     */
    data object OnBackClick : RagConfigScreenEvent

    /**
     * Пользователь изменил значение параметра "Top-K Before" —
     * количества документов, подаваемых на вход фильтру/реранкеру.
     *
     * @param value новое целочисленное значение.
     */
    data class OnTopKBeforeChanged(val value: Int) : RagConfigScreenEvent

    /**
     * Пользователь выбрал другой тип фильтрации.
     *
     * @param value новый выбранный тип фильтра {@link RagFilterType}.
     */
    data class OnFilterTypeChanged(val value: RagFilterType) : RagConfigScreenEvent

    /**
     * Пользователь изменил состояние переключателя использования
     * переписывания запроса (query rewriting).
     *
     * @param value включено ли переписывание запроса.
     */
    data class OnUseQueryRewritingChanged(val value: Boolean) : RagConfigScreenEvent

    /**
     * Пользователь изменил значение параметра "Top-K After" —
     * количества документов, передаваемых генеративной модели после
     * этапа фильтрации/ранжирования.
     *
     * @param value новое целочисленное значение.
     */
    data class OnTopKAfterChanged(val value: Int) : RagConfigScreenEvent

    /**
     * Пользователь изменил состояние переключателя «Использовать
     * минимальное сходство» для фильтрации документов по порогу.
     *
     * @param value включена ли фильтрация по минимальному сходству.
     */
    data class OnUseMinSimilarityChanged(val value: Boolean) : RagConfigScreenEvent

    /**
     * Пользователь изменил числовое значение минимального порога
     * косинусного сходства для отбора документов.
     *
     * @param value новое значение порога (от 0.0 до 1.0).
     */
    data class OnMinSimilarityChanged(val value: Double) : RagConfigScreenEvent

    /**
     * Пользователь инициировал выбор пути к файлу модели-реранкера.
     */
    data object OnChooseRankerModelPathClick : RagConfigScreenEvent

    /**
     * Пользователь вручную изменил путь к файлу модели-реранкера.
     *
     * @param value новый путь в строковом виде.
     */
    data class OnRankerModelPathChanged(val value: String) : RagConfigScreenEvent

    /**
     * Пользователь инициировал выбор пути к файлу токенизатора
     * модели-реранкера.
     */
    data object OnChooseRankerTokenizerPathClick : RagConfigScreenEvent

    /**
     * Пользователь вручную изменил путь к файлу токенизатора
     * модели-реранкера.
     *
     * @param value новый путь в строковом виде.
     */
    data class OnRankerTokenizerPathChanged(val value: String) : RagConfigScreenEvent

    /**
     * Пользователь инициировал выбор пути к файлу модели эмбеддингов.
     */
    data object OnChooseEmbModelPathClick : RagConfigScreenEvent

    /**
     * Пользователь вручную изменил путь к файлу модели эмбеддингов.
     *
     * @param value новый путь в строковом виде.
     */
    data class OnEmbModelPathChanged(val value: String) : RagConfigScreenEvent

    /**
     * Пользователь инициировал выбор пути к файлу токенизатора
     * модели эмбеддингов.
     */
    data object OnChooseEmbTokenizerPathClick : RagConfigScreenEvent

    /**
     * Пользователь вручную изменил путь к файлу токенизатора
     * модели эмбеддингов.
     *
     * @param value новый путь в строковом виде.
     */
    data class OnEmbTokenizerPathChanged(val value: String) : RagConfigScreenEvent
}
