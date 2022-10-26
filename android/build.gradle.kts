plugins {
    id("org.jetbrains.compose") version Lib.AndroidX.COMPOSE_VERSION
    id("com.android.application")
    kotlin("android")
}

group = ProjectProperties.APPLICATION_ID
version = "1.0"

repositories {
    jcenter()
    mavenLocal()
    mavenCentral()
}

@OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
dependencies {
    api(Lib.Decompose.core)
    implementation(project(Lib.Project.common))
    implementation(Lib.AndroidX.ACTIVITY_COMPOSE)
    implementation(Lib.AndroidX.SPLASH_SCREEN)
    implementation(Lib.Grpc.OKHTTP)
    api(Lib.Project.SLACK_DATA_ANDROID)
    api(Lib.Project.SLACK_DOMAIN_ANDROID)

    androidTestImplementation(
        Lib.AndroidX.COMPOSE_JUNIT
    )
    debugImplementation(
        Lib.AndroidX.COMPOSE_TEST_MANIFEST
    )
    androidTestImplementation(TestLib.JUNIT)
    androidTestImplementation(TestLib.RUNNER)
    implementation(compose.uiTestJUnit4)
}

android {
    compileSdk = ProjectProperties.COMPILE_SDK
    packagingOptions {
        resources.excludes.add("google/protobuf/*.proto")
    }
    defaultConfig {
        versionCode = 1
        versionName = "1.0"
        applicationId = "dev.baseio.slackclone"
        minSdk = ProjectProperties.MIN_SDK
        targetSdk = ProjectProperties.TARGET_SDK
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
