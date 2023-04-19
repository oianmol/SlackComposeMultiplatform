plugins {
    id(libs.plugins.kotlin.native.cocoapods.get().pluginId)
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.rick.nativecoroutines)
}

group = "dev.baseio.slackclone.common"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    commonMainApi(project(":slack_domain_layer"))
    commonMainApi(project(":slack_data_layer"))
}

kotlin {
    android()
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }

    cocoapods {
        summary = "Slack Common library"
        homepage = "https://github.com/oianmol"
        ios.deploymentTarget = "14.1"
        framework {
            baseName = "common"
            isStatic = true
        }
    }

    js(IR) {
        browser()
    }

    @Suppress("OPT_IN_USAGE")
    wasm {
        browser()
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {

                implementation(libs.datetime)
                implementation(libs.sqldelight.runtime)
                implementation(libs.koin.core)
                implementation(libs.coroutines)
                implementation(kotlin("stdlib-common"))
                implementation(libs.decompose.core)
            }
        }
        val jsWasmMain by creating {
            dependsOn(commonMain)
        }
        val jsMain by getting {
            dependsOn(jsWasmMain)
        }
        val wasmMain by getting {
            dependsOn(jsWasmMain)
        }
        val androidMain by getting {
            dependencies {
                // CameraX
                api("androidx.camera:camera-camera2:1.3.0-alpha04")
                api("androidx.camera:camera-lifecycle:1.3.0-alpha04")
                api("androidx.camera:camera-view:1.3.0-alpha04")
                api("androidx.camera:camera-video:1.3.0-alpha04")
                api("androidx.camera:camera-extensions:1.3.0-alpha04")
                implementation("com.google.guava:guava:29.0-android")
                // Zxing
                api("com.google.zxing:core:3.5.0")

                implementation("com.google.mlkit:barcode-scanning:17.0.3")
                api(libs.activity.compose)
                api("androidx.lifecycle:lifecycle-runtime-ktx:2.6.0")
                implementation("com.google.firebase:firebase-core:21.1.1")
                implementation("com.google.firebase:firebase-messaging:23.1.2")
                implementation("com.google.firebase:firebase-messaging-ktx:23.1.2")
                implementation(libs.koin.android)
                implementation(libs.coroutines)
                implementation(libs.lifecycleviewmodelktx)
                implementation(libs.securitycrypto)
                implementation(libs.coroutines.android)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.coroutines)
                implementation(libs.coroutines.swing)
                implementation("io.ktor:ktor-client-java:2.1.0")
                implementation(libs.koin.core.jvm)
                api("com.google.protobuf:protobuf-java:3.21.9")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.koin.test)
                implementation(kotlin("test"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(libs.coroutines.test)
                implementation("app.cash.turbine:turbine:0.12.1")
                implementation("dev.icerock.moko:test-core:0.6.1")
            }
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
                implementation(libs.sqldelight.jvmdriver)
                implementation(libs.sqldelight.androiddriver)
                implementation(libs.androidx.junit.ext.ktx)
                implementation(libs.coroutines.test)
                implementation(libs.grpc.okhttp)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(libs.grpc.okhttp)
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.2")
                implementation(libs.sqldelight.jvmdriver)
                implementation(libs.coroutines.test)
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting

        val iosMain by creating {
            dependsOn(commonMain)
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
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }
    namespace = "dev.baseio.slackcommon"
}
