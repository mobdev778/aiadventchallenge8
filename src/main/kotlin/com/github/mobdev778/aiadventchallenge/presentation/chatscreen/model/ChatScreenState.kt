package com.github.mobdev778.aiadventchallenge.presentation.chatscreen.model

import com.github.mobdev778.aiadventchallenge.domain.chat.model.Chat
import com.github.mobdev778.aiadventchallenge.domain.task.TaskContext

/**
 * Состояние экрана чата (ChatScreen).
 *
 * Содержит все данные, необходимые для отображения интерфейса чата,
 * включая информацию о самом чате, список отображаемых сообщений,
 * стратегию управления контекстом, ввод пользователя и связанный контекст задачи.
 *
 * @property chat Доменная модель текущего чата.
 * @property messages Список UI-представлений сообщений с иерархией и статусами видимости.
 * @property contextManagementState Текущее состояние управления контекстом сообщений
 *                                  (None, SlidingWindow, StickFacts, ветвление).
 * @property inputText Текст, введённый пользователем в поле ввода.
 * @property taskContext Контекст выполняемой задачи, если чат связан с задачей; иначе null.
 * @property autoPlay Флаг автоматического воспроизведения ответов модели.
 */
data class ChatScreenState(
    val chat: Chat,
    val messages: List<ChatUiMessage>,
    val contextManagementState: ContextManagementState,
    val inputText: String,
    val taskContext: TaskContext?,
    val autoPlay: Boolean,
)
