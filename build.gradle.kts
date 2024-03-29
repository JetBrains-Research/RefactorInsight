import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.jetbrains.intellij.IntelliJPluginExtension

plugins {
    java
    id("org.jetbrains.intellij") version "1.12.0"
    id("org.jetbrains.kotlin.jvm") version "1.8.0"
}

group = "org.jetbrains.research.refactorinsight"
version = "2023.3-1.0"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

subprojects {
    parent?.repositories?.forEach { repositories.add(it) }

    pluginManager.withPlugin("org.jetbrains.intellij") {
        intellij(configureIntelliJ)
    }

    tasks.withType<Test> {
        useJUnit()
        testLogging.exceptionFormat = FULL
    }
}

val configureIntelliJ: Action<IntelliJPluginExtension> = Action {
    version.set("LATEST-EAP-SNAPSHOT")
    plugins.set(listOf("com.intellij.java", "Git4Idea", "org.jetbrains.plugins.github", "org.jetbrains.kotlin"))
    downloadSources.set(true)
}

intellij(configureIntelliJ)

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}