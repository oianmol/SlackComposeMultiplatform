plugins {
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose)
    alias(libs.plugins.googleservices)
}

repositories {
    jcenter()
    mavenCentral()
}

kotlin {
    android()
    sourceSets {
        val androidMain by getting {
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            dependencies {
                api(libs.decompose.core)
                implementation(project(":commoncomposeui"))
                implementation(project(":common"))
                implementation(libs.activity.compose)
                implementation(libs.splash.screen)
                implementation(libs.grpc.okhttp)
                api(project(":slack_data_layer"))
                api(project(":slack_domain_layer"))
                implementation(libs.koin.core)
                implementation(libs.koin.android)


                // Firebase
                implementation("com.google.firebase:firebase-messaging-ktx:23.1.2")

                // Accompanist Permissions
                implementation(libs.accompanist.permission)
            }
        }

        @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.koin.test)
                implementation(compose.uiTestJUnit4)

                implementation(
                    libs.compose.junit
                )
                implementation(
                    libs.compose.test.manifest
                )
                implementation(libs.junit)
            }
        }
    }
}


android {
    compileSdk = 33
    packagingOptions {
        resources.excludes.add("google/protobuf/*.proto")
    }
    defaultConfig {
        versionCode = 1
        versionName = "1.0"
        applicationId = "dev.baseio.slackclone"
        minSdk = 24
        targetSdk = 33
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
