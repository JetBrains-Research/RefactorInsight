plugins {
    java
    id("org.jetbrains.intellij") version "1.10.1"
}

group = "org.jetbrains.research.refactorinsight"
version = "2022.3-1.0"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("com.github.JetBrains-Research:kotlinRMiner:v1.2")
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.13.0.202109080827-r")
    implementation("org.eclipse.jdt:org.eclipse.jdt.core:3.16.0")
    implementation("org.apache.commons:commons-text:1.10.0")
    implementation("org.kohsuke:github-api:1.313")
    implementation(group = "com.github.tsantalis", name = "refactoring-miner", version = "2.3.2") {
        exclude("org.slf4j", "slf4j-log4j12")
    }
    implementation("org.slf4j:slf4j-jdk14:1.7.30")
    testImplementation("junit:junit:4.13.2")
    testImplementation(group = "org.mockito", name = "mockito-core", version = "4.10.0")
}

intellij {
    version.set("2022.3")
    plugins.set(listOf("com.intellij.java", "Git4Idea", "org.jetbrains.plugins.github"))
    downloadSources.set(true)
}