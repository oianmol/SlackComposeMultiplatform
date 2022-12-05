plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.7.20"
    id("java")
}

repositories {
    gradlePluginPortal()
    google()
}


tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}