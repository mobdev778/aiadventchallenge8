package com.github.mobdev778.aiadventchallenge.domain.agent.agents.document

import com.github.mobdev778.aiadventchallenge.data.settings.repository.SettingsRepository
import com.github.mobdev778.aiadventchallenge.domain.agent.AgentOrchestrator
import com.github.mobdev778.aiadventchallenge.domain.agent.agents.BaseAgent
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentContext
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentRequest
import com.github.mobdev778.aiadventchallenge.domain.agent.model.AgentResponse
import com.github.mobdev778.aiadventchallenge.domain.chatclient.ChatClient
import com.github.mobdev778.aiadventchallenge.domain.invariant.InvariantRegistry
import com.github.mobdev778.aiadventchallenge.domain.mcpserver.McpServerInteractor
import com.github.mobdev778.aiadventchallenge.presentation.app.ProjectContainer
import com.intellij.openapi.application.readAction
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFileSystemItem
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiRecursiveElementWalkingVisitor
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.jetbrains.rd.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlin.collections.set
import kotlin.collections.sortedBy

/**
 * Агент, отвечающий за автоматическое документирование всего проекта.
 *
 * В процессе обработки запроса (метод [handle]) агент строит граф зависимостей между
 * Kotlin-файлами проекта с помощью [buildDependencyGraph], определяет узлы без документации,
 * отправляет их на черновую проработку (`draftDocumentFiles`) и последующее полное
 * документирование (`documentFiles`). Для генерации текстов документации используются
 * запросы к другим агентам через [AgentOrchestrator.asyncRequest], а для анализа
 * структуры проекта – PSI-дерево IntelliJ IDEA, предоставляемое через [ProjectContainer].
 *
 * Наследуется от [BaseAgent][com.github.mobdev778.aiadventchallenge.domain.agent.agents.BaseAgent],
 * что даёт доступ к реестру инвариантов, репозиторию настроек, клиенту чата и
 * интерактору MCP-серверов. Основное предназначение – пакетное документирование
 * всей кодовой базы в ответ на команду пользователя.
 *
 * @constructor Создаёт экземпляр агента с переданными зависимостями.
 *   [projectContainer] обязателен для доступа к PSI-дереву проекта.
 */
internal class DocumentProjectAgent(
    id: String,
    invariantRegistry: InvariantRegistry,
    settingsRepository: SettingsRepository,
    chatClient: ChatClient,
    mcpServerInteractor: McpServerInteractor,
    scope: CoroutineScope,
    private val projectContainer: ProjectContainer,
) : BaseAgent(id, invariantRegistry, settingsRepository, chatClient, mcpServerInteractor, scope) {

    override suspend fun getAgentRules(): String = ""

    /**
     * Основной метод обработки запроса на документирование проекта.
     *
     * Получает проект из [ProjectContainer], ищет все файлы с расширением `.kt`,
     * строит ориентированный граф зависимостей между файлами, основанный на PSI-ссылках.
     * Затем выбирает до 60 файлов без документации, отправляет их на черновое описание
     * и полное документирование посредством отправки асинхронных запросов в [AgentOrchestrator].
     * Возвращает ответ с количеством обработанных файлов.
     *
     * @param orchestrator Оркестратор, через который выполняется маршрутизация запросов к другим агентам.
     * @param context Текущий контекст агента (профиль, состояние задачи, инструменты и т.д.).
     * @param request Исходный запрос пользователя, содержащий идентификаторы чата и задачи.
     * @return Ответ [AgentResponse] с сообщением о количестве обработанных файлов.
     */
    override suspend fun handle(
        orchestrator: AgentOrchestrator,
        context: AgentContext,
        request: AgentRequest
    ): AgentResponse {
        println("!!! DocumentProjectAgent")

        val project = projectContainer.project!!
        val psiManager = PsiManager.getInstance(project)
        val kotlinFileType = requireNotNull(findKotlinFileType()) {
            "Kotlin file type is unavailable. Ensure the Kotlin plugin is enabled in the IDE."
        }
        val ktFiles = readAction {
            FileTypeIndex.getFiles(kotlinFileType, GlobalSearchScope.projectScope(project))
                .mapNotNull { psiManager.findFile(it) as? PsiFileSystemItem }
                .toSet()
        }

        val nodes = buildDependencyGraph(ktFiles)

        val sorted = nodes.values
            .filter { !it.documented }
            .sortedBy { it.links.size }
            .take(60)

        draftDocumentFiles(orchestrator, request.chatId, sorted)
        documentFiles(orchestrator, request.chatId, sorted)

        return AgentResponse(
            intermediate = false,
            agent = id,
            request = request,
            message = "Обработано файлов: ${ktFiles.size}",
            toolMessages = emptyList(),
            requestTokens = 0,
            responseTokens = 0,
            taskContext = null,
        )
    }

    /**
     * Строит граф зависимостей между Kotlin-файлами проекта.
     *
     * Обходит каждый файл из [ktFiles] с помощью [visitNode], собирая в [KtGraphNode.links]
     * узлы, на которые есть ссылки (через PSI-резолвинг). Одновременно для каждого узла
     * проверяется наличие KDoc-комментариев: если файл уже содержит документацию,
     * узел помечается как `documented` и его описание заполняется из существующего KDoc.
     *
     * @param ktFiles Набор PSI-элементов файловой системы, представляющих Kotlin-файлы проекта.
     * @return Ассоциативный массив, сопоставляющий канонический путь файла с соответствующим узлом [KtGraphNode].
     */
    suspend fun buildDependencyGraph(
        ktFiles: Set<PsiFileSystemItem>,
    ): Map<String, KtGraphNode> {
        println("!!! buildDependencyGraph")

        val nodes = HashMap<String, KtGraphNode>()
        for (file in ktFiles) {
            val path = file.virtualFile.canonicalPath ?: ""
            nodes[path] = KtGraphNode(path, file)
        }
        for (file in ktFiles) {
            val node = nodes[file.virtualFile.canonicalPath]
            node?.let {
                visitNode(nodes, it)
                if (isDocumented(node.item)) {
                    val fileContent = readAction {
                        VfsUtilCore.loadText(node.item.virtualFile)
                    }
                    val description = fileContent.substringAfter("/**").substringBeforeLast("*/")
                    node.description = description ?: ""
                }
                node.documented = node.description.isNotEmpty()
            }
        }
        return nodes
    }

    private suspend fun draftDocumentFiles(
        orchestrator: AgentOrchestrator,
        chatId: UUID,
        nodes: List<KtGraphNode>,
    ) {
        println("!!! draftDocumentFiles")
        val requestIdNodeMap = HashMap<UUID, KtGraphNode>()
        val pathIdMap = HashMap<String, UUID>()
        for (node in nodes) {
            val id = UUID.randomUUID()
            requestIdNodeMap[id] = node
            pathIdMap[node.path] = id
        }

        // 1. Запускаем сбор ответов ДО отправки запросов, чтобы не пропустить их
        val responsesDeferred = scope.async(Dispatchers.IO) {
            orchestrator.responses
                .filter { it.intermediate }
                .filter { requestIdNodeMap.containsKey(it.request.parentMessageId) }
                .take(nodes.size)
                .toList()
        }

        // 2. Отправляем запросы в очередь
        for (node in nodes) {
            orchestrator.asyncRequest(
                AgentRequest(
                    chatId = chatId,
                    taskContextId = null,
                    parentMessageId = pathIdMap[node.path],
                    time = System.currentTimeMillis(),
                    query = "/draft-document-file ${node.path}"
                )
            )
        }

        // 3. Ждем, пока все агенты вернут ответ и записываем описание в каждый отдельный узел
        val responses = responsesDeferred.await()
        for (response in responses) {
            val node = requestIdNodeMap[response.request.parentMessageId]
            if (node != null) {
                node.description = response.message
            }
        }
    }

    private suspend fun documentFiles(
        orchestrator: AgentOrchestrator,
        chatId: UUID,
        nodes: List<KtGraphNode>,
    ) {
        println("!!! documentFiles: ${nodes.map { it.path }}")
        val requestIdNodeMap = HashMap<UUID, KtGraphNode>()
        val pathIdMap = HashMap<String, UUID>()
        for (node in nodes) {
            val id = UUID.randomUUID()
            requestIdNodeMap[id] = node
            pathIdMap[node.path] = id
        }

        // 1. Запускаем сбор ответов ДО отправки запросов, чтобы не пропустить их
        val responsesDeferred = scope.async(Dispatchers.IO) {
            orchestrator.responses
                .filter { it.intermediate }
                .filter { requestIdNodeMap.containsKey(it.request.parentMessageId) }
                .take(nodes.size)
                .toList()
        }

        // 2. Отправляем запросы в очередь
        for (node in nodes) {
            orchestrator.asyncRequest(
                AgentRequest(
                    chatId = chatId,
                    taskContextId = null,
                    parentMessageId = pathIdMap[node.path],
                    time = System.currentTimeMillis(),
                    query = "/document-file ${node.path}",
                    meta = node,
                )
            )
        }

        // 3. Ждем, пока все агенты вернут ответ и обновляем каждый отдельный узел
        responsesDeferred.await()
    }

    private suspend fun isDocumented(item: PsiFileSystemItem): Boolean {
        return readAction {
            // Находим PsiFile, так как PsiFileSystemItem может быть директорией
            val psiFile = item.containingFile ?: return@readAction false
            var hasDoc = false

            // Обходим дерево PSI стандартным визитором IntelliJ
            psiFile.accept(object : PsiRecursiveElementWalkingVisitor() {
                override fun visitElement(element: PsiElement) {
                    // Ищем KDoc по имени класса ноды (подходит и для старого K1, и для нового K2 компилятора)
                    val className = element.javaClass.name
                    if (className.contains("KDoc")) {
                        hasDoc = true
                        stopWalking() // Прерываем обход файла, если нашли хоть один док
                        return
                    }
                    if (className.contains("KDocComment")) {
                        hasDoc = true
                        stopWalking()
                        return
                    }
                    super.visitElement(element)
                }
            })
            hasDoc
        }
    }

    private suspend fun visitNode(nodes: HashMap<String, KtGraphNode>, node: KtGraphNode) {
        if (node.visited) {
            return
        }
        node.visited = true

        val linkedChildren = readAction {
            val children = LinkedHashSet<KtGraphNode>()
            node.item.accept(object : PsiRecursiveElementWalkingVisitor() {
                override fun visitElement(element: PsiElement) {
                    for (reference in element.references) {
                        val resolvedTarget = reference.resolve()
                        val targetFile = resolvedTarget?.containingFile ?: continue
                        val child = nodes[targetFile.virtualFile.canonicalPath] ?: continue
                        if (child !== node) {
                            children.add(child)
                        }
                    }
                    super.visitElement(element)
                }
            })
            children.toList()
        }

        for (child in linkedChildren) {
            visitNode(nodes, child)
            node.links.add(child)
        }
    }

    private fun findKotlinFileType(): FileType? {
        return FileTypeManager.getInstance().registeredFileTypes.firstOrNull { fileType ->
            fileType.defaultExtension == "kt" || fileType.name.equals("Kotlin", ignoreCase = true)
        }
    }
}
