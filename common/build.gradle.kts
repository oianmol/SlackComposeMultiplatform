import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlin-parcelize")
    id("org.jetbrains.compose") version "1.2.0"
    kotlin("plugin.serialization") version "1.7.20"
}

group = "dev.baseio.slackclone"
version = "1.0"

val ktor_version = "2.1.0"

object Jvm {
    val target = JavaVersion.VERSION_1_8
}

object Versions {
    const val koin = "3.1.4"
}

object Deps {

    object Decompose {
        const val VERSION = "1.0.0-alpha-06"
        const val core = "com.arkivanov.decompose:decompose:$VERSION"
        const val composejb = "com.arkivanov.decompose:extensions-compose-jetbrains:$VERSION-native-compose"
    }

    object Kotlinx {
        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4"
        const val datetime = "org.jetbrains.kotlinx:kotlinx-datetime:0.4.0"

        object JVM {
            const val coroutinesSwing = "org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4"
        }

        object IOS {
            const val coroutinesX64 = "org.jetbrains.kotlinx:kotlinx-coroutines-core-iosx64:1.6.4"
            const val coroutinesArm64 = "org.jetbrains.kotlinx:kotlinx-coroutines-core-iosarm64:1.6.4"
        }

        object Android {
            const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4"
        }
    }

    object SqlDelight {
        const val androidDriver = "com.squareup.sqldelight:android-driver:1.5.3"
        const val jvmDriver = "com.squareup.sqldelight:sqlite-driver:1.5.3"
        const val nativeDriver = "com.squareup.sqldelight:native-driver:1.5.3"
        const val core = "com.squareup.sqldelight:runtime:1.5.3"
    }

    object Koin {
        const val core = "io.insert-koin:koin-core:${Versions.koin}"
        const val core_jvm = "io.insert-koin:koin-core-jvm:${Versions.koin}"
        const val test = "io.insert-koin:koin-test:${Versions.koin}"
        const val android = "io.insert-koin:koin-android:${Versions.koin}"
    }

    object AndroidX {
        const val lifecycleViewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1"
    }
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    commonMainApi("dev.icerock.moko:paging:0.7.2")
}

kotlin {
    val iosEnabled = true
    targets(iosEnabled)

    sourceSets {
        commonDependencies(this@kotlin)
        androidDependencies(this@kotlin)
        jvmDependencies(this@kotlin)
        if (iosEnabled) {
            iosArmDependencies()
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
        // iosSimulatorArm64()
        iosX64()
    }
}

fun NamedDomainObjectContainer<org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet>.commonDependencies(
    kotlinMultiplatformExtension: KotlinMultiplatformExtension
) {
    val commonMain by getting {
        dependencies {
            implementation("dev.baseio.slackclone:slack_kmp_domain:1.0")
            implementation("dev.baseio.slackclone:slack_kmp_data:1.0")
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
            implementation("com.squareup.sqldelight:runtime:1.5.3")
            implementation(Deps.Koin.core)
            api(kotlinMultiplatformExtension.compose.runtime)
            api(kotlinMultiplatformExtension.compose.foundation)
            api(kotlinMultiplatformExtension.compose.material)
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
            implementation(Deps.Kotlinx.datetime)
            implementation(Deps.SqlDelight.core)
            implementation(Deps.Kotlinx.coroutines)
            implementation(Deps.Koin.core)
            implementation(kotlin("stdlib-common"))
            implementation(Deps.Decompose.core)
            implementation(Deps.Decompose.composejb)
        }
    }
    val commonTest by getting {
        dependencies {
            implementation(Deps.Koin.test)
            implementation(kotlin("test"))
        }
    }
}

fun NamedDomainObjectContainer<org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet>.androidDependencies(
    kotlinMultiplatformExtension: KotlinMultiplatformExtension
) {
    val androidMain by getting {
        dependencies {
            implementation("dev.baseio.slackclone:slack_kmp_domain-android:1.0")
            implementation("dev.baseio.slackclone:slack_kmp_data-android:1.0")
            implementation(Deps.Koin.android)
            implementation(Deps.Kotlinx.coroutines)
            implementation(Deps.AndroidX.lifecycleViewModelKtx)
            implementation("androidx.security:security-crypto-ktx:1.1.0-alpha03")
            implementation("com.google.accompanist:accompanist-systemuicontroller:0.26.3-beta")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
            implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
            implementation("io.coil-kt:coil-compose:2.2.0")
            implementation("io.ktor:ktor-client-android:$ktor_version")
            api("androidx.constraintlayout:constraintlayout-compose:1.0.1")
            api("androidx.appcompat:appcompat:1.5.1")
            api("androidx.core:core-ktx:1.9.0")
            implementation(Deps.Decompose.composejb)
        }
    }
    val androidTest by getting {
        dependencies {
            implementation("junit:junit:4.13.2")
        }
    }
}

fun NamedDomainObjectContainer<org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet>.iosArmDependencies() {
    val iosArm64Main by getting {
        dependencies {
            implementation("dev.baseio.slackclone:slack_kmp_domain-iosarm64:1.0")
            implementation("dev.baseio.slackclone:slack_kmp_data-iosarm64:1.0")
            implementation("io.ktor:ktor-client-darwin:$ktor_version")
        }
    }
}

fun NamedDomainObjectContainer<org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet>.jvmDependencies(
    kotlinMultiplatformExtension: KotlinMultiplatformExtension
) {
    val jvmMain by getting {
        dependencies {
            implementation("dev.baseio.slackclone:slack_kmp_domain-jvm:1.0")
            implementation("dev.baseio.slackclone:slack_kmp_data-jvm:1.0")
            implementation(Deps.Kotlinx.coroutines)
            implementation(Deps.Kotlinx.JVM.coroutinesSwing)
            implementation("io.ktor:ktor-client-java:$ktor_version")
            implementation("com.alialbaali.kamel:kamel-image:0.4.0")
            api(kotlinMultiplatformExtension.compose.preview)
            implementation(Deps.Koin.core_jvm)
        }
    }
}

fun NamedDomainObjectContainer<org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet>.iosX64Dependencies() {
    val iosX64Main by getting {
        dependencies {
            implementation("dev.baseio.slackclone:slack_kmp_domain-iosx64:1.0")
            implementation("dev.baseio.slackclone:slack_kmp_data-iosx64:1.0")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-iosx64:1.6.4")
            implementation("io.ktor:ktor-client-darwin:$ktor_version")
        }
    }
}
