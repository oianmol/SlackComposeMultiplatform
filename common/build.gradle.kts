import Lib.AndroidX.ACTIVITY_COMPOSE
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    kotlin(BuildPlugins.MULTIPLATFORM)
    id(BuildPlugins.ANDROID_LIBRARY_PLUGIN)
    id(BuildPlugins.KOTLIN_PARCELABLE_PLUGIN)
    kotlin("native.cocoapods")
    id(BuildPlugins.COMPOSE_ID) version Lib.AndroidX.COMPOSE_VERSION
    kotlin(BuildPlugins.SERIALIZATION) version Lib.Kotlin.KOTLIN_VERSION
}

group = ProjectProperties.APPLICATION_ID
version = "1.0"


repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("androidx.test:core-ktx:1.4.0")
    commonMainApi(Lib.Multiplatform.mokoPaging)
}

kotlin {
    val iosEnabled = true
    targets(iosEnabled)

    cocoapods {
        version = "1.0"
        summary = ""
        homepage = ""
        framework {
            baseName = "capillary-ios"
        }
        ios.deploymentTarget = "14.1"
        pod("Tink", version = "~> 1.6.1", moduleName = "Tink")
    }

    sourceSets {
        commonDependencies(this@kotlin)
        androidDependencies()
        jvmDependencies(this@kotlin)
        configureTest(this@kotlin)
        if (iosEnabled) {
            iosArmDependencies()
            iosSimulatorArmDependencies()
            iosX64Dependencies()
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
    }
    packagingOptions {
        resources.excludes.add("google/protobuf/*.proto")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

fun KotlinMultiplatformExtension.targets(iosEnabled: Boolean = true) {
    android()
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    if (iosEnabled) {
        iosArm64()
        iosSimulatorArm64()
        iosX64()
    }
}

fun NamedDomainObjectContainer<org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet>.commonDependencies(
    kotlinMultiplatformExtension: KotlinMultiplatformExtension
) {
    val commonMain by getting {
        dependencies {
            implementation(project(Lib.Project.SLACK_DOMAIN_COMMON))
            implementation(project(Lib.Project.SLACK_DATA_COMMON))
            implementation(project(Lib.Project.CAPILLARY_KMP))
            implementation(Deps.Kotlinx.datetime)
            implementation(Deps.SqlDelight.runtime)
            implementation(Deps.Koin.core)
            api(kotlinMultiplatformExtension.compose.runtime)
            api(kotlinMultiplatformExtension.compose.foundation)
            api(kotlinMultiplatformExtension.compose.material)
            implementation(Deps.Kotlinx.datetime)
            implementation(Deps.SqlDelight.runtime)
            implementation(Lib.Async.COROUTINES)
            implementation(Deps.Koin.core)
            implementation(kotlin("stdlib-common"))
            implementation(Lib.Decompose.core)
            implementation(Lib.Decompose.composejb)
        }
    }
}

fun NamedDomainObjectContainer<org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet>.androidDependencies() {
    val androidMain by getting {
        dependencies {

            // CameraX
            api("androidx.camera:camera-camera2:1.2.0-rc01")
            api ("androidx.camera:camera-lifecycle:1.2.0-rc01")
            api ("androidx.camera:camera-view:1.2.0-rc01")
            api ("androidx.camera:camera-video:1.2.0-rc01")

            // Zxing
            api("com.google.zxing:core:3.5.0")
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
}

fun NamedDomainObjectContainer<org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet>.iosArmDependencies() {
    val iosArm64Main by getting {
        dependencies {
            implementation(Lib.Decompose.composejb)
        }
    }
}

fun NamedDomainObjectContainer<org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet>.iosSimulatorArmDependencies(){
    val iosSimulatorArm64Main by getting {
        dependencies {
            implementation(Lib.Decompose.composejb)
        }
    }
}


fun NamedDomainObjectContainer<org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet>.iosX64Dependencies() {
    val iosX64Main by getting {
        dependencies {
            implementation(Lib.Decompose.composejb)
        }
    }
}

fun NamedDomainObjectContainer<org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet>.jvmDependencies(
    kotlinMultiplatformExtension: KotlinMultiplatformExtension
) {
    val jvmMain by getting {
        dependencies {
            implementation(Lib.Async.COROUTINES)
            implementation(Deps.Kotlinx.JVM.coroutinesSwing)
            implementation("io.ktor:ktor-client-java:2.1.0")
            implementation(Lib.Multiplatform.kamelImage)
            api(kotlinMultiplatformExtension.compose.preview)
            implementation(Deps.Koin.core_jvm)
            implementation(Lib.Decompose.composejb)
            api("com.google.protobuf:protobuf-java:3.21.6")

            implementation("com.google.crypto.tink:tink:1.7.0") {
                exclude("com.google.protobuf", module = "*")
            }
            implementation("com.google.crypto.tink:apps-webpush:1.7.0") {
                exclude("com.google.crypto.tink", module = "*")
            }
        }
    }

}

fun NamedDomainObjectContainer<org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet>.configureTest(kotlinMultiplatformExtension: KotlinMultiplatformExtension) {
    val commonTest by getting {
        dependencies {
            implementation(Deps.Koin.test)
            implementation(kotlin("test"))
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
            implementation(Lib.Async.COROUTINES_TEST)
            implementation("app.cash.turbine:turbine:0.12.0")
            implementation("dev.icerock.moko:test-core:0.6.1")
        }
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
    val androidTest by getting {
        dependencies {
            implementation(kotlin("test-junit"))
            implementation("junit:junit:4.13.2")
            implementation(Deps.SqlDelight.jvmDriver)
            implementation(Deps.SqlDelight.androidDriver)
            implementation(TestLib.ROBO_ELECTRIC)
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
}
