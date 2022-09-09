import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.2.0-alpha01-dev755"
    id("com.android.library")
    id("com.squareup.sqldelight")
}

group = "dev.baseio.slackclone"
version = "1.0"

val ktor_version = "2.1.0"
val DECOMPOSE = "1.0.0-alpha-04"

object Versions {
    const val koin = "3.1.4"
}

object Deps {

    object Koin {
        const val core = "io.insert-koin:koin-core:${Versions.koin}"
        const val core_jvm = "io.insert-koin:koin-core-jvm:${Versions.koin}"
        const val test = "io.insert-koin:koin-test:${Versions.koin}"
        const val android = "io.insert-koin:koin-android:${Versions.koin}"
    }

}

kotlin {
    android()
    val iosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget = when {
        System.getenv("SDK_NAME")?.startsWith("iphoneos") == true -> ::iosArm64
        System.getenv("NATIVE_ARCH")?.startsWith("arm") == true -> ::iosSimulatorArm64 // available to KT 1.5.30
        else -> ::iosX64
    }
    iosTarget("iOS") {}
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    sourceSets {

        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
                implementation("com.squareup.sqldelight:runtime:1.5.3")
                implementation(Deps.Koin.core)
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
            }
        }
        val sqlDriverNativeMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation("com.squareup.sqldelight:native-driver:1.5.3")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(Deps.Koin.test)
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Deps.Koin.android)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
                implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
                implementation("com.squareup.sqldelight:android-driver:1.5.3")
                implementation("io.coil-kt:coil-compose:2.2.0")
                api("androidx.constraintlayout:constraintlayout-compose:1.0.1")
                implementation("io.ktor:ktor-client-android:$ktor_version")
                api("androidx.appcompat:appcompat:1.5.0")
                api("androidx.core:core-ktx:1.8.0")
            }
        }
        val iOSMain by getting {
            dependsOn(sqlDriverNativeMain)
            dependencies {
                // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-core-iosx64
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-iosx64:1.6.4")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-iosarm64:1.6.4")

                implementation("io.ktor:ktor-client-darwin:$ktor_version")
                implementation("com.squareup.sqldelight:native-driver:1.5.3")
            }
        }

        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation("com.alialbaali.kamel:kamel-image:0.4.0")
                implementation("com.squareup.sqldelight:sqlite-driver:1.5.3")
                implementation("io.ktor:ktor-client-java:$ktor_version")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4")
                api(compose.preview)
                implementation(Deps.Koin.core_jvm)
            }
        }
        val desktopTest by getting
    }
}

sqldelight {
    database("SlackDB") {
        packageName = "dev.baseio.database"
        linkSqlite = true
    }
}


android {
    compileSdk = 32
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 24
        targetSdk = 32
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

kotlin {
    targets.withType<KotlinNativeTarget> {
        binaries.all {
            // TODO: the current compose binary surprises LLVM, so disable checks for now.
            freeCompilerArgs += "-Xdisable-phases=VerifyBitcode"
        }
    }
}