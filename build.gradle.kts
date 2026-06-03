import java.net.URI

plugins {
    kotlin("plugin.serialization") version "2.0.0"
    id("com.github.gmazzo.buildconfig") version "5.3.5"
    kotlin("jvm") version "2.2.21"
    application
}

group = "com.embeddings.rag"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = URI.create("https://jitpack.io") }
}

dependencies {
    implementation("io.insert-koin:koin-core:3.5.6")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:2.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    implementation("com.squareup.okhttp3:okhttp:5.3.2")
    implementation("com.squareup.okhttp3:logging-interceptor:5.3.2")

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(24)
}

application {
    mainClass.set("com.github.mobdev778.aiadventchallenge.app.MainKt")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaExec>().configureEach {
    if (name == "run") {
        inputs.property("profile", profile)
    }
}

val profile = project.findProperty("profile")?.toString() ?: "dev"
logger.lifecycle("Building application profile: $profile")

val profileConfig = when (profile) {
    "prod" -> {
        val prodApiKey = System.getenv("AI_PROXY_API_KEY")
        requireNotNull(prodApiKey)
        val prodBaseUrl = System.getenv("AI_PROXY_BASE_URL")
        requireNotNull(prodBaseUrl)
        val prodModel = System.getenv("AI_PROXY_MODEL")
        mapOf(
            "API_KEY" to prodApiKey,
            "BASE_URL" to "${prodBaseUrl}/v1/",
            "MODEL" to prodModel
        )
    }
    "dev" -> {
        mapOf(
            "API_KEY" to "",
            "BASE_URL" to "http://127.0.0.1:1234/v1/",
            "MODEL" to "qwen/qwen3-14b"
        )
    }
    else -> throw GradleException("Unknown profile: $profile")
}

buildConfig {
    className("BuildConfig")
    packageName(project.group.toString())

    buildConfigField("String", "API_KEY", "\"${profileConfig["API_KEY"]}\"")
    buildConfigField("String", "BASE_URL", "\"${profileConfig["BASE_URL"]}\"")
    buildConfigField("String", "MODEL", "\"${profileConfig["MODEL"]}\"")
}