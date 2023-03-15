plugins {
    kotlin(BuildPlugins.MULTIPLATFORM)
    id(BuildPlugins.ANDROID_APPLICATION_PLUGIN)
    id(BuildPlugins.COMPOSE_ID)
    id("com.google.gms.google-services")
}

repositories {
    jcenter()
    mavenLocal()
    mavenCentral()
}

kotlin {
    android()
    sourceSets {
        val androidMain by getting {
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


                // Firebase
                implementation(Lib.Firebase.CLOUD_MESSAGING)

                // Accompanist Permissions
                implementation(Lib.AndroidX.ACCOMPANIST_PERMISSION)
            }
        }

        @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
        val androidUnitTest by getting {
            dependencies {
                implementation(Deps.Koin.test)
                implementation(compose.uiTestJUnit4)

                implementation(
                    Lib.AndroidX.COMPOSE_JUNIT
                )
                implementation(
                    Lib.AndroidX.COMPOSE_TEST_MANIFEST
                )
                implementation(TestLib.JUNIT)
            }
        }
    }
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
