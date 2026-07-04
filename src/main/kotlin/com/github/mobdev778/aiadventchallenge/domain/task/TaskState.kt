package com.github.mobdev778.aiadventchallenge.domain.task

enum class TaskState {
    Planning,    // собираем требования, утверждаем план
    Execution,   // выполняем задачу (пишем код, создаем артефакты)
    Validation,  // тесты, ревью, соответствие плану
    PrintResult, // выведи результат
    Done,        // задача завершена
}
