plugins {
    java
    id("org.jetbrains.intellij") version "1.8.0"
}

group = "org.jetbrains.research.refactorinsight"
version = "2022.2-1.0"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation("com.github.JetBrains-Research:kotlinRMiner:v1.2")
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.2.1.201812262042-r")
    implementation("org.slf4j:slf4j-log4j12:1.7.7")
    implementation("org.eclipse.jdt:org.eclipse.jdt.core:3.16.0")
    implementation("org.apache.commons:commons-text:1.6")
    implementation("org.kohsuke:github-api:1.95")
    implementation(group = "com.github.tsantalis", name = "refactoring-miner", version = "2.0")
    testImplementation("junit:junit:4.13")
    testImplementation(group = "org.mockito", name = "mockito-core", version = "4.7.0")
}

intellij {
    version.set("2022.2.1")
    plugins.set(listOf("com.intellij.java", "git4idea", "github"))
    downloadSources.set(true)
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = "11"
        targetCompatibility = "11"
        options.compilerArgs.add("-Xlint:all")
    }
}