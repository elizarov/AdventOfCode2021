plugins {
    kotlin("jvm") version "1.6.0"
}

repositories {
    mavenCentral()
}

sourceSets.main {
    java.srcDirs("src")
}

tasks.wrapper {
    gradleVersion = "7.3"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = listOf("-opt-in=kotlin.time.ExperimentalTime", "-opt-in=kotlin.RequiresOptIn")
    }
}