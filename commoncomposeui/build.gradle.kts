import Lib.AndroidX.ACTIVITY_COMPOSE

plugins {
    kotlin(BuildPlugins.MULTIPLATFORM)
    id(BuildPlugins.ANDROID_LIBRARY_PLUGIN)
    id(BuildPlugins.KOTLIN_PARCELABLE_PLUGIN)
    kotlin("native.cocoapods")
    id("com.rickclephas.kmp.nativecoroutines")
    id("org.kodein.mock.mockmp") version "1.10.0"
    id(BuildPlugins.COMPOSE_ID) version Lib.AndroidX.COMPOSE_VERSION
    kotlin(BuildPlugins.SERIALIZATION) version Lib.Kotlin.KOTLIN_VERSION
}

group = ProjectProperties.APPLICATION_ID
version = "1.0"

mockmp {
    usesHelper = true
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    commonMainApi(Lib.Multiplatform.mokoPaging)
    commonMainApi(project(Lib.Project.SLACK_DOMAIN_COMMON))
    commonMainApi(project(Lib.Project.SLACK_DATA_COMMON))
}

kotlin {
    android()
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "Slack commoncomposeui library"
        homepage = "https://github.com/oianmol"
        ios.deploymentTarget = "14.1"
        framework {
            baseName = "commoncomposeui"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(Lib.Project.common))
                implementation(Deps.Kotlinx.datetime)
                implementation(Deps.SqlDelight.runtime)
                implementation(Deps.Koin.core)
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                implementation(Lib.Async.COROUTINES)
                implementation(kotlin("stdlib-common"))
                implementation(Lib.Decompose.core)
                implementation(Lib.Decompose.composejb)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(Deps.Koin.test)
                implementation(kotlin("test"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(Lib.Async.COROUTINES_TEST)
                implementation("app.cash.turbine:turbine:0.12.1")
                implementation("dev.icerock.moko:test-core:0.6.1")
            }
        }

        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
                implementation(Deps.SqlDelight.jvmDriver)
                implementation(Deps.SqlDelight.androidDriver)
                implementation(TestLib.ANDROID_JUNIT)
                implementation(Lib.Async.COROUTINES_TEST)
                implementation(Lib.Grpc.OKHTTP)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(Lib.Grpc.OKHTTP)
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
                implementation(Deps.SqlDelight.jvmDriver)
                implementation(Lib.Async.COROUTINES_TEST)
            }
        }
        val androidMain by getting {
            dependencies {
                // CameraX
                api("androidx.camera:camera-camera2:1.3.0-alpha01")
                api("androidx.camera:camera-lifecycle:1.3.0-alpha01")
                api("androidx.camera:camera-view:1.3.0-alpha01")
                api("androidx.camera:camera-video:1.3.0-alpha01")
                api("androidx.camera:camera-extensions:1.3.0-alpha01")
                implementation("com.google.guava:guava:29.0-android")
                // Zxing
                api("com.google.zxing:core:3.5.0")

                implementation("com.google.mlkit:barcode-scanning:17.0.2")
                api(ACTIVITY_COMPOSE)
                api("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
                implementation("com.google.crypto.tink:tink-android:1.7.0") {
                    exclude(group = "com.google.protobuf")
                }
                implementation("com.google.crypto.tink:apps-webpush:1.7.0") {
                    exclude("com.google.crypto.tink", module = "*")
                }
                implementation("com.google.firebase:firebase-core:21.1.1")
                implementation("com.google.firebase:firebase-messaging:23.1.0")
                implementation("com.google.firebase:firebase-messaging-ktx:23.1.0")
                implementation(Deps.Koin.android)
                implementation(Lib.Async.COROUTINES)
                implementation(Deps.AndroidX.lifecycleViewModelKtx)
                implementation(Lib.AndroidX.securityCrypto)
                implementation(Lib.AndroidX.ACCOMPANIST_SYSTEM_UI_CONTROLLER)
                implementation(Lib.Async.COROUTINES_ANDROID)
                implementation(Lib.AndroidX.COIL_COMPOSE)
                implementation(Lib.Decompose.composejb)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(Lib.Async.COROUTINES)
                implementation(Deps.Kotlinx.JVM.coroutinesSwing)
                api("io.ktor:ktor-client-java:2.1.0")
                api(Lib.Multiplatform.kamelImage)
                api(compose.preview)
                implementation(Deps.Koin.core_jvm)
                implementation(Lib.Decompose.composejb)
                api("com.google.protobuf:protobuf-java:3.21.9")

                implementation("com.google.crypto.tink:tink:1.7.0") {
                    exclude("com.google.protobuf", module = "*")
                }
                implementation("com.google.crypto.tink:apps-webpush:1.7.0") {
                    exclude("com.google.crypto.tink", module = "*")
                }
            }
        }
        val iosX64Main by getting{
            dependencies{
                implementation(Lib.AndroidX.COMPOSE_RUNTIME_SAVEABLE_uikitx64)
            }
        }
        val iosArm64Main by getting{
            dependencies{
                implementation(Lib.AndroidX.COMPOSE_RUNTIME_SAVEABLE_uikitarm64)
            }
        }
        val iosSimulatorArm64Main by getting{
            dependencies{
                implementation(Lib.AndroidX.COMPOSE_RUNTIME_SAVEABLE_uikitsimarm64)
            }
        }

        val iosMain by creating {
            dependsOn(commonMain)
            dependencies{
                implementation(Lib.Decompose.composejb)
            }
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}
kotlin {
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.all {
            // TODO: the current compose binary surprises LLVM, so disable checks for now.
            freeCompilerArgs += "-Xdisable-phases=VerifyBitcode"
        }
    }
}

android {
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 24
        targetSdk = 33
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
    packagingOptions {
        resources.excludes.add("google/protobuf/*.proto")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks.replace("podGenIOS", PatchedPodGenTask::class)