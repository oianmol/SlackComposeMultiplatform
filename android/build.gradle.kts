plugins {
    id("org.jetbrains.compose") version "1.2.0-alpha01-dev774"
    id("com.android.application")
    kotlin("android")
}

group = "dev.baseio.slackclone"
version = "1.0"
val slackDataVersion: String by project

repositories {
    jcenter()
    mavenLocal()
}

dependencies {
    implementation(project(":common"))
    implementation("androidx.activity:activity-compose:1.5.1")
    implementation("androidx.core:core-splashscreen:1.0.0")
}

android {
    compileSdk = 33
    defaultConfig {
        versionCode = 1
        versionName = "1.0"
        applicationId = "dev.baseio.slackclone"
        minSdk = 24
        targetSdk = 33
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}