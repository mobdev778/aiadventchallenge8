# AIAdventChallenge8

**AI-ассистент для IntelliJ IDEA** — плагин, предоставляющий встроенного AI-помощника с поддержкой чата, интеграцией по протоколу [MCP (Model Context Protocol)](https://modelcontextprotocol.io/), системой RAG (Retrieval-Augmented Generation) и многоагентной архитектурой.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.0-blueviolet.svg?logo=kotlin)](https://kotlinlang.org/)
[![IntelliJ Platform](https://img.shields.io/badge/IntelliJ%20Platform-2026.1.3-blue.svg?logo=intellijidea)](https://plugins.jetbrains.com/docs/intellij/welcome.html)

---

## Оглавление

- [Возможности](#возможности)
- [Архитектура](#архитектура)
- [Структура проекта](#структура-проекта)
- [Технологический стек](#технологический-стек)
- [Требования](#требования)
- [Сборка и запуск](#сборка-и-запуск)
- [Использование](#использование)
- [Настройка](#настройка)
- [MCP-серверы](#mcp-серверы)
- [RAG-система](#rag-система)
- [Агентная система](#агентная-система)
- [Стратегии управления контекстом](#стратегии-управления-контекстом)
- [Инварианты безопасности](#инварианты-безопасности)
- [Профили](#профили)
- [Документация](#документация)
- [Лицензия](#лицензия)

---

## Возможности

### AI-чат
- Полноценное общение с AI-моделями в режиме реального времени
- Поддержка **Markdown** и **подсветки синтаксиса** (`CodeFence`) в ответах
- **Стриминг** ответов (построчная выдача токенов)
- Автоматическая прокрутка к последнему сообщению
- Переключение между несколькими чатами в одном окне
- Отображение состояния контекст-менеджмента (использование контекстного окна)

### MCP (Model Context Protocol)
- Подключение **внешних MCP-серверов** через HTTP/SSE
- **Встроенные MCP-серверы** плагина:
  - [`MyMcpReadFileServer`](src/main/kotlin/com/github/mobdev778/aiadventchallenge/domain/mymcpserver/MyMcpReadFileServer.kt) — чтение файлов проекта для AI-агентов
  - [`MyMcpSaveToFileServer`](src/main/kotlin/com/github/mobdev778/aiadventchallenge/domain/mymcpserver/MyMcpSaveToFileServer.kt) — сохранение сгенерированного кода в файлы
  - [`MyMcpRagSearchServer`](src/main/kotlin/com/github/mobdev778/aiadventchallenge/domain/mymcpserver/rag/MyMcpRagSearchServer.kt) — поиск по RAG-документам
  - [`MyMcpRagChatServer`](src/main/kotlin/com/github/mobdev778/aiadventchallenge/domain/mymcpserver/ragchat/MyMcpRagChatServer.kt) — RAG-чат с историей сообщений
- Кэширование инструментов MCP-серверов для быстрого доступа
- Автоматический запуск серверов при старте IDE (флаг `launchAtStartup`)

### RAG (Retrieval-Augmented Generation)
- Добавление **документов** (файлы проекта, внешние URL)
- Разбиение на чанки с настраиваемыми стратегиями (фиксированный размер / по абзацам)
- **Эмбеддинги** (`all-MiniLM-L6-v2` через LangChain4J)
- **Ранжирование** результатов поиска:
  - `SimilarityRanker` — косинусное сходство
  - `HeuristicRanker` — эвристическая оценка релевантности
  - `ReRanker` — ONNX реранкинг (LangChain4J)
- **Перезапись запросов** (Query Rewriter) для улучшения качества поиска
- **Фильтрация** результатов по языку (русский фильтр)

### Многоагентная система
- **ChatAssistantAgent** — основной агент для диалога
- **PlannerAgent** — планирование сложных задач
- **ExecutorAgent** — выполнение запланированных шагов
- **ValidatorAgent** — валидация результатов выполнения
- **SummarizerAgent** — суммаризация длинных контекстов
- **DocumentFileAgent** / **DocumentProjectAgent** / **DraftDocumentFileAgent** — агенты для работы с файлами проекта и черновиками

### Система рассуждений (Reasoning Strategies)
- **DirectAnswerStrategy** — прямой ответ без дополнительных шагов
- **StepByStepStrategy** — пошаговое рассуждение (Chain-of-Thought)
- **PanelOfExpertsStrategy** — симуляция панели экспертов
- **MetaPromptStrategy** — мета-промптинг

### Управление контекстом
Настраиваемые стратегии для эффективного использования контекстного окна LLM:
- **SlidingWindow** — скользящее окно (последние N сообщений)
- **Branching** — ветвление диалога с построением дерева сообщений
- **RecursiveSummation** — рекурсивная суммаризация старых сообщений
- **StickyFacts** — сохранение ключевых фактов (sticky facts) в виде карты текст → текст

### Безопасность
- **StackGuard** — защита от бесконечных рекурсивных вызовов инструментов
- **BannedWords** — фильтрация запрещённых слов в запросах и ответах
- **InvariantRegistry** — реестр проверок инвариантов безопасности

### Дополнительно
- **Генерация изображений** через GPT (DALL-E)
- **Профили** — создание и переключение между кастомными системными промптами
- **Логирование** — полное логирование работы плагина в файлы с возможностью просмотра в UI
- **Гибкие настройки** — API-ключ, базовый URL, модель, max-токены, контекст-менеджмент

---

## Архитектура

Проект построен по принципам **Clean Architecture** с тремя основными слоями:

```
┌─────────────────────────────────────────────────────────┐
│                    PRESENTATION                          │
│  (Jetpack Compose UI, State Holders, Events, Commands)  │
├─────────────────────────────────────────────────────────┤
│                      DOMAIN                              │
│  (Use Cases, Domain Models, Interfaces, Business Logic) │
├─────────────────────────────────────────────────────────┤
│                       DATA                               │
│  (Room DB, Retrofit API, Repositories, Mappers)         │
└─────────────────────────────────────────────────────────┘
```

### Поток данных

```
User Action → Event → StateHolder → Domain (Use Case / Interactor)
    → Data (Repository) → DataSource (Room / REST API)
    → Response mapper → Domain model → State → UI recomposition
```

### DI-контейнер

Внедрение зависимостей реализовано через **Koin** с аннотациями **KSP**:

- Модули [`data.*.datasource.*DatabaseModule`](src/main/kotlin/com/github/mobdev778/aiadventchallenge/data/) — базы данных Room
- [`NetworkModule`](src/main/kotlin/com/github/mobdev778/aiadventchallenge/data/network/NetworkModule.kt) — сетевые клиенты (Retrofit, OkHttp)
- [`AppModule`](src/main/kotlin/com/github/mobdev778/aiadventchallenge/presentation/app/AppModule.kt) — общие зависимости
- KSP автоматически генерирует модули из аннотаций `@Single` / `@Factory`

---

## Структура проекта

```
src/main/kotlin/com/github/mobdev778/aiadventchallenge/
├── data/                                # Слой данных
│   ├── chat/                            # Хранение чатов и сообщений (Room)
│   │   ├── datasource/
│   │   │   ├── ChatDao.kt               # DAO для чатов
│   │   │   ├── ChatAppDatabase.kt       # БД чатов
│   │   │   ├── ChatDatabaseModule.kt    # Koin-модуль
│   │   │   ├── StickyFactsDao.kt        # DAO для sticky-фактов
│   │   │   └── model/                   # Entity-классы
│   │   └── repository/
│   │       ├── ChatRepository.kt
│   │       └── StickyFactsRepository.kt
│   ├── chatclient/                      # OpenAI-совместимый REST-клиент
│   │   ├── datasource/
│   │   │   ├── ChatRestApi.kt           # Retrofit-интерфейс
│   │   │   └── model/                   # DTO-классы
│   │   └── repository/
│   │       ├── ChatClientRepository.kt
│   │       └── *Mapper.kt               # Мапперы DTO ↔ Domain
│   ├── imageclient/                     # REST-клиент для генерации изображений
│   │   ├── datasource/
│   │   │   ├── ImageRestApi.kt
│   │   │   └── model/
│   │   └── repository/
│   ├── mcpserver/                       # Хранение MCP-серверов (Room)
│   │   ├── datasource/
│   │   │   ├── McpServerDao.kt
│   │   │   ├── McpServerAppDatabase.kt
│   │   │   └── model/
│   │   └── repository/
│   ├── network/                         # Сетевой модуль
│   │   └── NetworkModule.kt             # OkHttp, Retrofit, Ktor
│   ├── profile/                         # Хранение профилей (Room)
│   │   ├── datasource/
│   │   └── repository/
│   ├── rag/                             # Хранение RAG-документов (Room)
│   │   ├── datasource/
│   │   │   ├── RagAppDatabase.kt
│   │   │   ├── RagDocumentDao.kt
│   │   │   └── model/
│   │   └── repository/
│   │       ├── RagChatRepository.kt     # Чат-история RAG
│   │       ├── RagConfigRepository.kt   # Конфигурации RAG
│   │       └── RagDocumentRepository.kt # Документы и чанки
│   ├── settings/                        # Хранение настроек (Room)
│   │   ├── datasource/
│   │   │   ├── SettingsDao.kt
│   │   │   ├── SettingsAppDatabase.kt
│   │   │   └── model/
│   │   └── repository/
│   └── taskcontext/                     # Хранение контекста задач (Room)
│       ├── datasource/
│       └── repository/
├── domain/                              # Слой бизнес-логики
│   ├── agent/                           # Агентная система
│   │   ├── AgentFactory.kt             # Фабрика агентов
│   │   ├── AgentOrchestrator.kt        # Оркестратор агентов
│   │   ├── agents/                     # Реализации агентов
│   │   │   ├── Agent.kt               # Интерфейс агента
│   │   │   ├── BaseAgent.kt           # Базовый агент
│   │   │   ├── ChatAssistantAgent.kt  # Ассистент чата
│   │   │   ├── ExecutorAgent.kt       # Исполнитель задач
│   │   │   ├── PlannerAgent.kt        # Планировщик
│   │   │   ├── SummarizerAgent.kt     # Суммаризатор
│   │   │   ├── ValidatorAgent.kt      # Валидатор
│   │   │   ├── SystemPromptBuilder.kt # Сборщик системного промпта
│   │   │   └── document/              # Агенты для работы с документами
│   │   ├── model/                     # Доменные модели агентов
│   │   │   ├── AgentContext.kt        # Контекст агента
│   │   │   ├── AgentRequest.kt        # Запрос к агенту
│   │   │   ├── AgentResponse.kt       # Ответ агента
│   │   │   ├── AgentType.kt           # Типы агентов
│   │   │   └── ToolResponse.kt        # Ответ инструмента
│   │   └── pool/                      # Пул агентов и очередь запросов
│   │       ├── AgentContextBuilder.kt
│   │       ├── AgentPool.kt
│   │       └── AgentRequestQueue.kt
│   ├── chat/                           # Бизнес-логика чата
│   │   ├── ChatInteractor.kt
│   │   ├── ObserveWindowMessagesUseCase.kt
│   │   └── model/
│   ├── chatclient/                     # Интерфейс AI-клиента
│   ├── contextmanagement/              # Стратегии управления контекстом
│   │   ├── ContextManagementStrategy.kt
│   │   ├── NoStrategy.kt
│   │   ├── branching/                 # Ветвление
│   │   ├── recursivesummation/        # Рекурсивная суммаризация
│   │   ├── slidingwindow/             # Скользящее окно
│   │   ├── stickyFacts/               # Ключевые факты
│   │   └── tree/                      # Дерево сообщений
│   ├── imageclient/                    # Интерфейс клиента изображений
│   ├── imagegenerator/                 # Генерация изображений (GPT)
│   │   └── gpt/GptImageGenerator.kt
│   ├── invariant/                      # Инварианты безопасности
│   │   ├── BannedWords.kt
│   │   ├── Invariant.kt
│   │   ├── InvariantRegistry.kt
│   │   ├── StackGuard.kt
│   │   └── ValidationResult.kt
│   ├── mcpserver/                      # Бизнес-логика MCP
│   │   ├── CachedMcpToolsChecker.kt
│   │   ├── McpServerInteractor.kt
│   │   ├── McpToolsChecker.kt
│   │   └── model/
│   ├── mymcpserver/                    # Встроенные MCP-серверы
│   │   ├── BaseMyMcpServer.kt        # Базовый сервер (Ktor)
│   │   ├── MyMcpReadFileServer.kt    # Чтение файлов
│   │   ├── MyMcpSaveToFileServer.kt  # Сохранение в файлы
│   │   ├── MyMcpServer.kt            # Интерфейс MyMcpServer
│   │   ├── MyMcpServerInteractor.kt  # Управление серверами
│   │   ├── rag/                       # RAG-поиск (MCP-сервер)
│   │   ├── ragchat/                   # RAG-чат (MCP-сервер)
│   │   └── model/
│   ├── profile/                        # Доменные модели профилей
│   ├── rag/                            # Бизнес-логика RAG
│   │   ├── RagChunkGenerator.kt       # Генератор чанков
│   │   ├── RagSearcher.kt            # Интерфейс поиска
│   │   ├── RagSearchResult.kt        # Результат поиска
│   │   ├── RankedRagSearcher.kt      # Ранжированный поиск
│   │   ├── SimpleRagSearcher.kt      # Простой поиск
│   │   ├── filechunker/              # Стратегии разбиения файлов
│   │   ├── model/                    # Доменные модели RAG
│   │   ├── queryrewriter/            # Перезапись запросов
│   │   └── ranker/                   # Ранкеры
│   │       ├── CosineSimilarityExt.kt
│   │       ├── HeuristicRanker.kt
│   │       ├── Ranker.kt
│   │       ├── RankerFactory.kt
│   │       ├── ReRanker.kt
│   │       └── SimilarityRanker.kt
│   ├── reasoningstrategy/              # Стратегии рассуждений
│   │   ├── DirectAnswerStrategy.kt
│   │   ├── MetaPromptStrategy.kt
│   │   ├── PanelOfExpertsStrategy.kt
│   │   └── StepByStepStrategy.kt
│   ├── settings/                       # Бизнес-логика настроек
│   └── task/                           # Контекст задач
├── infrastructure/                     # Инфраструктурный слой
│   └── logging/                        # Логирование
│       ├── PluginLoggerFactory.kt      # SLF4J-фабрика
│       ├── PluginLogFileManager.kt     # Менеджер лог-файлов
│       └── PluginUncaughtExceptionHandler.kt
└── presentation/                       # Слой UI (Compose Multiplatform)
    ├── addmcpserverscreen/             # Экран добавления MCP-сервера
    ├── addprofilescreen/               # Экран добавления профиля
    ├── app/                            # Точка входа и роутинг
    │   ├── AppModule.kt               # Koin-модуль приложения
    │   ├── AppRouter.kt               # Маршрутизатор экранов
    │   ├── AppToolWindowFactory.kt    # Фабрика окна инструментов
    │   ├── PluginInitializer.kt       # Инициализация плагина
    │   ├── PluginShutdownHandlerService.kt
    │   └── ProjectContainer.kt        # Контейнер проекта
    ├── chatlistscreen/                 # Экран списка чатов
    ├── chatscreen/                     # Экран чата
    │   ├── composable/
    │   │   ├── ChatMessageRow.kt       # Сообщение чата
    │   │   ├── ChatInputArea.kt        # Поле ввода
    │   │   ├── CodeFenceBlock.kt       # Блок кода с подсветкой
    │   │   ├── MarkdownParser.kt       # Парсер Markdown
    │   │   ├── ChatSwitcher.kt         # Переключатель чатов
    │   │   ├── ContextManagementIndicator.kt
    │   │   └── ...
    │   └── model/
    ├── common/                         # Общие UI-компоненты
    │   ├── Dot.kt
    │   ├── ScreenHeader.kt
    │   └── TaskStateIndicator.kt
    ├── editprofilescreen/              # Экран редактирования профиля
    ├── logsscreen/                     # Экран просмотра логов
    ├── mcpinfoscreen/                  # Экран информации об MCP-сервере
    ├── mcpserverlistscreen/            # Экран списка MCP-серверов
    ├── mymcpserverscreen/              # Экран «Мои MCP-серверы»
    ├── profilelistscreen/              # Экран списка профилей
    ├── rag/                            # RAG UI
    │   ├── addingragdocumentscreen/    # Экран добавления документа
    │   ├── addragdocumentscreen/       # Экран ввода параметров документа
    │   ├── ragconfigscreen/            # Экран конфигурации RAG
    │   ├── ragdocumentlistscreen/      # Экран списка документов
    │   └── viewragdocumentscreen/      # Экран просмотра документа
    ├── settingsscreen/                 # Экран настроек
    │   ├── composable/
    │   │   ├── LlmSettingsBlock.kt
    │   │   ├── ContextManagementTypeTypeBlock.kt
    │   │   └── ...
    │   └── model/
    └── taskcontextscreen/              # Экран контекста задачи
```

---

## Технологический стек

| Категория | Технология | Версия |
|---|---|---|
| **Язык** | Kotlin (JVM) | 2.3.0 |
| **Платформа** | IntelliJ Platform | 2026.1.3 |
| **UI** | Jetpack Compose (IntelliJ) | — |
| **DI** | Koin + KSP Annotations | 4.2.2 / 2.3.1 |
| **База данных** | Room (SQLite) | 2.7.0 |
| **Сеть** | Retrofit + OkHttp | 2.11.0 / 5.3.2 |
| **Сериализация** | Kotlinx Serialization JSON | 1.6.3 |
| **MCP-клиент** | Kotlin SDK Client (JVM) | 0.14.0 |
| **MCP-сервер** | Kotlin SDK Server + Ktor Netty | 0.14.0 / 3.2.3 |
| **Эмбеддинги** | LangChain4J (all-MiniLM-L6-v2) | 1.0.0-beta1 |
| **Реранкинг** | LangChain4J ONNX Scoring | 1.0.0-beta1 |
| **Логирование** | SLF4J (собственная реализация) | 1.7.36 |
| **Статический анализ** | Detekt | 1.23.7 |
| **Документация** | Dokka | 1.9.20 |
| **Сборка** | Gradle (Kotlin DSL) | — |
| **JDK** | Java 21 | — |

---

## Требования

- **IntelliJ IDEA** 2026.1 или новее (сборка `252.25557+`)
- **JDK 21**
- **Gradle** 9.6+ (используется Gradle Wrapper — [`gradlew`](gradlew))

---

## Сборка и запуск

### 1. Клонирование репозитория

```bash
git clone https://github.com/mobdev778/aiadventchallenge8.git
cd aiadventchallenge8
```

### 2. Сборка плагина

```bash
./gradlew buildPlugin
```

Готовый `.zip` архив плагина будет находиться в `build/distributions/`.

### 3. Запуск в песочнице (Sandbox IDE)

```bash
./gradlew runIde
```

### 4. Установка плагина в IDE

1. Откройте IntelliJ IDEA
2. Перейдите в `Settings → Plugins → ⚙️ → Install Plugin from Disk...`
3. Выберите собранный `.zip` файл
4. Перезапустите IDE

### 5. Статический анализ (Detekt)

```bash
./gradlew detekt
```

### 6. Генерация документации (Dokka)

```bash
./gradlew dokkaHtml
```

Документация будет сгенерирована в `build/dokka/html/`.

---

## Использование

### Запуск

После установки плагина в правой нижней части IDE появится вкладка инструментов **"AI Adventure Challenge"** с двумя подвкладками:

- **AI Chat** — основное рабочее пространство
- **Logs** — просмотр логов работы плагина

### Начало работы

1. Откройте вкладку **"AI Chat"**
2. Перейдите в настройки (⚙️) и укажите:
   - **Base URL** — URL OpenAI-совместимого API (например, `https://api.openai.com/v1`)
   - **API Key** — ключ доступа к API
   - **Base Model** — модель (например, `gpt-4o`, `gpt-4o-mini`)
   - **Max Tokens** — максимальное количество токенов в ответе
3. Вернитесь на экран списка чатов и создайте новый чат (+)
4. Начните общение с AI-ассистентом

### Навигация по экранам

| Экран | Описание |
|---|---|
| **ChatList** | Список всех чатов |
| **Chat** | Окно чата с AI |
| **Settings** | Главные настройки (LLM, контекст) |
| **ProfileList** | Управление профилями (системные промпты) |
| **RagDocumentList** | Список RAG-документов |
| **RagConfig** | Конфигурация RAG-параметров |
| **AddRagDocument** | Добавление нового RAG-документа |
| **ViewRagDocument** | Просмотр содержимого RAG-документа |
| **McpServerList** | Список внешних MCP-серверов |
| **MyMcpServer** | Управление встроенными MCP-серверами |
| **McpInfo** | Детальная информация о MCP-сервере |
| **TaskContext** | Контекст выполняемой задачи |
| **Logs** | Просмотр логов |

---

## Настройка

### Параметры LLM

Доступны через [`SettingsScreen`](src/main/kotlin/com/github/mobdev778/aiadventchallenge/presentation/settingsscreen/SettingsScreen.kt):

| Параметр | Описание |
|---|---|
| **Base URL** | URL API (OpenAI-совместимый) |
| **API Key** | Ключ авторизации |
| **Base Model** | Модель по умолчанию (gpt-4o, gpt-4o-mini, и др.) |
| **Max Tokens** | Лимит токенов в ответе |
| **Reasoning Effort** | Уровень усилий рассуждения (low/medium/high) |

### Управление контекстом

Тип стратегии выбирается через [`ContextManagementType`](src/main/kotlin/com/github/mobdev778/aiadventchallenge/domain/settings/model/ContextManagementType.kt):

| Стратегия | Параметры |
|---|---|
| **SlidingWindow** | `Max Messages` — размер окна |
| **Branching** | Автоматическое ветвление |
| **RecursiveSummation** | `Max Messages` перед суммаризацией |
| **StickyFacts** | `Max Messages` для хранения фактов |
| **NoStrategy** | Без управления контекстом |

---

## MCP-серверы

### Внешние MCP-серверы

Плагин поддерживает подключение внешних MCP-серверов по HTTP/SSE:

1. Перейдите в `Settings → MCP Servers`
2. Добавьте новый сервер, указав URL и тип (HTTP/SSE)
3. Плагин автоматически подключится и загрузит список доступных инструментов

Проверка доступности инструментов выполняется через [`McpToolsChecker`](src/main/kotlin/com/github/mobdev778/aiadventchallenge/domain/mcpserver/McpToolsChecker.kt), а кэширование — через [`CachedMcpToolsChecker`](src/main/kotlin/com/github/mobdev778/aiadventchallenge/domain/mcpserver/CachedMcpToolsChecker.kt).

### Встроенные MCP-серверы

Встроенные серверы работают на Ktor (Netty) и доступны AI-агентам:

| Сервер | Порт по умолчанию | Описание |
|---|---|---|
| [`MyMcpReadFileServer`](src/main/kotlin/com/github/mobdev778/aiadventchallenge/domain/mymcpserver/MyMcpReadFileServer.kt) | 8081 | Чтение файлов проекта |
| [`MyMcpSaveToFileServer`](src/main/kotlin/com/github/mobdev778/aiadventchallenge/domain/mymcpserver/MyMcpSaveToFileServer.kt) | 8082 | Сохранение кода в файлы |
| [`MyMcpRagSearchServer`](src/main/kotlin/com/github/mobdev778/aiadventchallenge/domain/mymcpserver/rag/MyMcpRagSearchServer.kt) | 8083 | RAG-поиск по документам |
| [`MyMcpRagChatServer`](src/main/kotlin/com/github/mobdev778/aiadventchallenge/domain/mymcpserver/ragchat/MyMcpRagChatServer.kt) | 8084 | RAG-чат с историей |

Управление серверами — через экран **«My MCP Servers»**:
- Запуск/остановка вручную
- Автозапуск при старте IDE (`launchAtStartup`)
- Просмотр логов в реальном времени

---

## RAG-система

### Добавление документов

1. Перейдите в `Settings → RAG Documents`
2. Нажмите **«+»** для добавления нового документа
3. Укажите:
   - **Title** — название документа
   - **Source** — путь к файлу в проекте или URL
   - **Chunking Strategy** — стратегия разбиения (Fixed Size / Paragraphs)
4. Подтвердите и дождитесь индексации

### Поиск

Поиск использует AI-агентов через MCP-сервер [`MyMcpRagSearchServer`](src/main/kotlin/com/github/mobdev778/aiadventchallenge/domain/mymcpserver/rag/MyMcpRagSearchServer.kt):

1. Запрос перезаписывается для улучшения качества поиска ([`QueryRewriter`](src/main/kotlin/com/github/mobdev778/aiadventchallenge/domain/rag/queryrewriter/QueryRewriter.kt))
2. Генерируются эмбеддинги для запроса и чанков
3. Выполняется ранжирование (косинусное сходство + эвристика + реранкинг)
4. Отбираются наиболее релевантные чанки
5. Результаты возвращаются AI-ассистенту

### RAG-чат

[`MyMcpRagChatServer`](src/main/kotlin/com/github/mobdev778/aiadventchallenge/domain/mymcpserver/ragchat/MyMcpRagChatServer.kt) поддерживает полноценный диалог с сохранением истории сообщений и поиском по RAG-документам.

### Стратегии разбиения (Chunkers)

Реализации в пакете [`filechunker`](src/main/kotlin/com/github/mobdev778/aiadventchallenge/domain/rag/filechunker/):

| Чанкер | Файл | Описание |
|---|---|---|
| **FixedSize** | [`FixedSizeFileChunker.kt`](src/main/kotlin/com/github/mobdev778/aiadventchallenge/domain/rag/filechunker/FixedSizeFileChunker.kt) | Разбиение на чанки фиксированного размера |
| **Paragraphs** | [`ParagraphsFileChunker.kt`](src/main/kotlin/com/github/mobdev778/aiadventchallenge/domain/rag/filechunker/ParagraphsFileChunker.kt) | Разбиение по абзацам |

### Ранкеры

Реализации в пакете [`ranker`](src/main/kotlin/com/github/mobdev778/aiadventchallenge/domain/rag/ranker/), создаваемые через [`RankerFactory`](src/main/kotlin/com/github/mobdev778/aiadventchallenge/domain/rag/ranker/RankerFactory.kt):

| Ранкер | Описание |
|---|---|
| **SimilarityRanker** | Косинусное сходство эмбеддингов |
| **HeuristicRanker** | Эвристическая оценка (частота слов, позиция, близость) |
| **ReRanker** | ONNX реранкинг через LangChain4J |

---

## Агентная система

### Агенты

Агенты реализуют интерфейс [`Agent`](src/main/kotlin/com/github/mobdev778/aiadventchallenge/domain/agent/agents/Agent.kt) и создаются через [`AgentFactory`](src/main/kotlin/com/github/mobdev778/aiadventchallenge/domain/agent/AgentFactory.kt):

| Агент | ID | Назначение |
|---|---|---|
| **ChatAssistant** | `chat_assistant` | Основной диалоговый агент |
| **Planner** | `planner` | Планирование сложных задач |
| **Executor** | `executor` | Выполнение шагов плана |
| **Validator** | `validator` | Проверка результатов |
| **Summarizer** | `summarizer` | Суммаризация контекста |
| **DocumentFile** | `document_file` | Анализ отдельного файла |
| **DocumentProject** | `document_project` | Анализ всего проекта |
| **DraftDocumentFile** | `draft_document_file` | Создание черновика файла |

### Оркестрация

[`AgentOrchestrator`](src/main/kotlin/com/github/mobdev778/aiadventchallenge/domain/agent/AgentOrchestrator.kt) координирует работу агентов:

- **Planner** определяет план действий
- **Executor** выполняет шаги плана
- **Validator** проверяет результаты
- При необходимости план корректируется и цикл повторяется
- **Summarizer** агрегирует результаты в итоговый ответ пользователю

### Пул агентов и очередь запросов

[`AgentPool`](src/main/kotlin/com/github/mobdev778/aiadventchallenge/domain/agent/pool/AgentPool.kt) управляет жизненным циклом агентов, а [`AgentRequestQueue`](src/main/kotlin/com/github/mobdev778/aiadventchallenge/domain/agent/pool/AgentRequestQueue.kt) обеспечивает последовательную обработку запросов.

---

## Инварианты безопасности

Система проверок для безопасной работы AI:

| Инвариант | Файл | Описание |
|---|---|---|
| **StackGuard** | [`StackGuard.kt`](src/main/kotlin/com/github/mobdev778/aiadventchallenge/domain/invariant/StackGuard.kt) | Защита от рекурсивных вызовов инструментов с ограничением глубины стека |
| **BannedWords** | [`BannedWords.kt`](src/main/kotlin/com/github/mobdev778/aiadventchallenge/domain/invariant/BannedWords.kt) | Фильтр запрещённых слов в запросах и ответах |

Все инварианты регистрируются в [`InvariantRegistry`](src/main/kotlin/com/github/mobdev778/aiadventchallenge/domain/invariant/InvariantRegistry.kt) и проверяются перед каждым запросом к AI и после каждого ответа.

---

## Профили

Профили позволяют создавать и переключаться между различными системными промптами:

- **Профиль по умолчанию** — встроенный системный промпт «Chat Assistant»
- **Кастомные профили** — создаются через UI и могут содержать любые инструкции
- Профили хранятся в БД Room и доступны через [`ProfileRepository`](src/main/kotlin/com/github/mobdev778/aiadventchallenge/data/profile/repository/ProfileRepository.kt)

---

## Документация

Полная документация API (KDoc/Dokka) доступна в:

- **Локально**: после сборки (`./gradlew dokkaHtml`) → `build/dokka/html/`
- **В репозитории**: [`docs/`](docs/) (предсгенерированная HTML-документация)

Основные документированные модули:
- [`data.chatclient.datasource.model`](docs/-a-i-advent-challenge8/com.github.mobdev778.aiadventchallenge.data.chatclient.datasource.model/) — DTO-модели AI API
- [`data.settings.datasource`](docs/-a-i-advent-challenge8/com.github.mobdev778.aiadventchallenge.data.settings.datasource/) — настройки и БД
- [`domain.agent.agents`](docs/-a-i-advent-challenge8/com.github.mobdev778.aiadventchallenge.domain.agent.agents/) — агенты
- [`domain.agent.pool`](docs/-a-i-advent-challenge8/com.github.mobdev778.aiadventchallenge.domain.agent.pool/) — пул агентов

Диаграмма навигации по экранам: [`docs/navigation.html`](docs/navigation.html)

---

## Лицензия

MIT License.

---

## Контакты

- **Автор**: [mobdev778](https://github.com/mobdev778)
- **Репозиторий**: [aiadventchallenge8](https://github.com/mobdev778/aiadventchallenge8)
