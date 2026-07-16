package com.github.mobdev778.aiadventchallenge.presentation.mymcpserverscreen

/**
 * Запечатанный интерфейс, представляющий набор команд (пользовательских действий) для экрана MyMcpServer.
 * Все команды реализуют этот интерфейс, что позволяет использовать единую реакцию на события экрана.
 */
sealed interface MyMcpServerScreenCommand {
    /**
     * Команда для возврата на предыдущий экран приложения.
     */
    data object Back : MyMcpServerScreenCommand
}
