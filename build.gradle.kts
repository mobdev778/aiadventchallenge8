plugins {
    kotlin("plugin.serialization") version "2.3.0"
    id("org.jetbrains.kotlin.jvm") version "2.3.0"
    id("org.jetbrains.intellij.platform") version "2.10.2"
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.0"

    // Room (KMP/JVM) uses KSP for annotation processing
    id("com.google.devtools.ksp") version "2.3.9"
}

ksp {
    arg("KOIN_DEFAULT_MODULE", "true")
}

group = "com.github.mobdev778.aiadventchallenge"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.12.2"))

    // embedding
    implementation("dev.langchain4j:langchain4j-embeddings-all-minilm-l6-v2:1.0.0-beta1")
    // reranking
    implementation("dev.langchain4j:langchain4j-onnx-scoring:1.0.0-beta1")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("io.insert-koin:koin-core:4.2.2")

    // Koin annotations (KSP)
    implementation("io.insert-koin:koin-annotations:2.3.1")
    ksp("io.insert-koin:koin-ksp-compiler:2.3.1")

    // Room (KMP/JVM)
    implementation("androidx.room:room-runtime:2.7.0") {
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core")
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core-jvm")
    }
    implementation("androidx.sqlite:sqlite-bundled:2.5.0")
    ksp("androidx.room:room-compiler:2.7.0")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:2.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    implementation("com.squareup.okhttp3:okhttp:5.3.2")
    implementation("com.squareup.okhttp3:logging-interceptor:5.3.2")
    implementation("org.slf4j:slf4j-api:1.7.36")

    implementation("io.modelcontextprotocol:kotlin-sdk-client-jvm:0.9.0") {
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core")
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core-jvm")
    }

    implementation("io.ktor:ktor-client-cio-jvm:3.2.3") {
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core")
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core-jvm")
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-jdk8")
    }

    // зависимости для старта MCP-сервера
    implementation("io.modelcontextprotocol:kotlin-sdk-server-jvm:0.9.0") {
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core")
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core-jvm")
    }
    implementation("io.ktor:ktor-server-netty-jvm:3.2.3") {
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core")
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core-jvm")
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-jdk8")
    }
    implementation("io.ktor:ktor-server-sse-jvm:3.2.3") {
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core")
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core-jvm")
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-jdk8")
    }
    implementation("io.ktor:ktor-server-content-negotiation-jvm:3.2.3") {
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core")
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core-jvm")
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-jdk8")
    }
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:3.2.3") {
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core")
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core-jvm")
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-jdk8")
    }

    // IntelliJ Platform Gradle Plugin Dependencies Extension
    // (https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html)
    intellijPlatform {
        intellijIdea("2026.1.3")
        composeUI()
        bundledPlugin("org.jetbrains.kotlin")
        bundledLibrary("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "252.25557"
        }

        changeNotes = """
            Initial version
        """.trimIndent()
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "21"
        targetCompatibility = "21"
    }

    // Remove kotlin-stdlib from the sandbox — the IDE's bundled Kotlin plugin provides it.
    // Bundling our own copy causes LinkageError (ClosedFloatingPointRange loaded by
    // two different PluginClassLoader instances).
    val removeKotlinStdlib by registering {
        notCompatibleWithConfigurationCache("Removes files from sandbox directory at execution time")
        doLast {
            val libDir = file("build/idea-sandbox/IU-2026.1.3/plugins/AIAdventChallenge8/lib")
            libDir.listFiles()?.filter {
                it.name.startsWith("kotlin-stdlib") || it.name.startsWith("kotlin-reflect")
            }?.forEach {
                it.delete()
                println("[prepareSandbox] Removed bundled Kotlin library: ${it.name}")
            }
        }
    }
    named("prepareSandbox") {
        finalizedBy(removeKotlinStdlib)
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

tasks.test {
    useJUnitPlatform()
}
