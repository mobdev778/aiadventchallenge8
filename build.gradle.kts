plugins {
    kotlin("plugin.serialization") version "2.0.0"
    id("org.jetbrains.kotlin.jvm") version "2.1.20"
    id("org.jetbrains.intellij.platform") version "2.10.2"
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.20"
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
    testImplementation(libs.junit)

    implementation("io.insert-koin:koin-core:3.5.6")

    // IMPORTANT (IntelliJ plugins):
    // Avoid bundling your own kotlinx-coroutines artifacts unless you really need to.
    // The IntelliJ Platform already provides coroutines; bundling another version can lead to
    // classloader constraint violations like:
    // LinkageError: ... collectAsState(StateFlow, ...) ... different Class objects for StateFlow
    //
    // implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:2.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    implementation("com.squareup.okhttp3:okhttp:5.3.2")
    implementation("com.squareup.okhttp3:logging-interceptor:5.3.2")

    // IntelliJ Platform Gradle Plugin Dependencies Extension
    // (https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html)
    intellijPlatform {
        intellijIdea("2025.3.5")
        composeUI()
    }

    // IMPORTANT:
    // `lifecycle-viewmodel-compose` pulls JetBrains Compose runtime (org.jetbrains.compose.*),
    // which conflicts with the Compose runtime bundled with the IntelliJ Platform (Jewel bridge).
    // For IntelliJ plugins, prefer IntelliJ Platform / Jewel APIs and avoid bringing your own Compose runtime.
    //
    // implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")

    // Same rationale as above: avoid bundling coroutines Swing unless required.
    // implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.8.1")
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
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

tasks.test {
    useJUnitPlatform()
}
