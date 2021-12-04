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
