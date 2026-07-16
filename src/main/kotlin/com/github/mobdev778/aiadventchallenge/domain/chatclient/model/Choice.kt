package com.github.mobdev778.aiadventchallenge.domain.chatclient.model

/**
 * Модель данных, представляющая один вариант ответа от API чат-завершения.
 * Содержит сгенерированное [сообщение][Message] и [причину завершения][FinishReason] генерации,
 * совместно описывая законченный результат работы модели.
 *
 * @param message Сообщение, сгенерированное моделью для данного варианта выбора.
 * @param finishReason Причина, по которой модель завершила генерацию (например, достигнут маркер конца,
 *                     исчерпан лимит токенов, вызваны инструменты или сработал фильтр).
 */
data class Choice(
    val message: Message,
    val finishReason: FinishReason,
)
