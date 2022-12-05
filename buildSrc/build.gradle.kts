plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.7.20"
    id("java")
}

repositories {
    gradlePluginPortal()
    google()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.20") {
        exclude("org.antlr")
    }
    implementation("com.android.tools.build:gradle:7.2.2") {
        exclude("org.antlr")
    }

    implementation("com.google.guava:guava:31.1-jre")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}


tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}