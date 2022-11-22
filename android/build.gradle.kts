plugins {
    id("org.jetbrains.compose") version Lib.AndroidX.COMPOSE_VERSION
    id("com.android.application")
    kotlin("android")
    id("com.google.gms.google-services")
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
    implementation(project(Lib.Project.commonComposeUI))
    implementation(project(Lib.Project.common))
    implementation(Lib.AndroidX.ACTIVITY_COMPOSE)
    implementation(Lib.AndroidX.SPLASH_SCREEN)
    implementation(Lib.Grpc.OKHTTP)
    api(project(Lib.Project.SLACK_DATA_COMMON))
    api(project(Lib.Project.SLACK_DOMAIN_COMMON))
    implementation(Deps.Koin.core)
    implementation(Deps.Koin.android)
    testImplementation(Deps.Koin.test)

    androidTestImplementation(
        Lib.AndroidX.COMPOSE_JUNIT
    )
    debugImplementation(
        Lib.AndroidX.COMPOSE_TEST_MANIFEST
    )
    androidTestImplementation(TestLib.JUNIT)
    implementation(compose.uiTestJUnit4)

    // Firebase
    implementation(platform(Lib.Firebase.BOM))
    implementation(Lib.Firebase.CLOUD_MESSAGING)

    // Accompanist Permissions
    implementation(Lib.AndroidX.ACCOMPANIST_PERMISSION)
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
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            pickFirsts += "**/*.pickFirst"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}
