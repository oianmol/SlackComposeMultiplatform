import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import io.github.timortel.kotlin_multiplatform_grpc_plugin.GrpcMultiplatformExtension.OutputTarget


plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.2.0-alpha01-dev774"
    id("com.android.library")
    id("com.squareup.sqldelight")
    kotlin("plugin.serialization") version "1.7.10"
    id("com.google.protobuf") version "0.8.18"
    id("io.github.timortel.kotlin-multiplatform-grpc-plugin") version "0.2.2"
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
val slackDataVersion: String by project


kotlin {
    android()
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    iosArm64()
    iosSimulatorArm64()
    iosX64()
    sourceSets {

        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
                implementation("com.squareup.sqldelight:runtime:1.5.3")
                implementation(Deps.Koin.core)
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
                implementation(Deps.Kotlinx.datetime)
                implementation(Deps.SqlDelight.core)
                implementation(Deps.Kotlinx.coroutines)
                implementation(Deps.Koin.core)
                implementation(kotlin("stdlib-common"))
                api("io.github.timortel:grpc-multiplatform-lib:0.2.2")
            }
            kotlin.srcDirs(
                projectDir.resolve("build/generated/source/kmp-grpc/commonMain/kotlin").canonicalPath,
            )
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
            kotlin.srcDirs(
                projectDir.resolve("build/generated/source/kmp-grpc/androidMain/kotlin").canonicalPath,
            )
            dependencies {
                implementation(Deps.Koin.android)
                implementation(project(":generate-proto"))
                implementation(Deps.Kotlinx.coroutines)
                implementation(Deps.SqlDelight.androidDriver)
                implementation(Deps.AndroidX.lifecycleViewModelKtx)
                implementation("androidx.security:security-crypto-ktx:1.1.0-alpha03")
                api("io.github.timortel:grpc-multiplatform-lib-android:0.2.2")
                implementation("com.google.accompanist:accompanist-systemuicontroller:0.26.3-beta")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
                implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
                implementation("com.squareup.sqldelight:android-driver:1.5.3")
                implementation("io.coil-kt:coil-compose:2.2.0")
                implementation("io.ktor:ktor-client-android:$ktor_version")
                api("androidx.constraintlayout:constraintlayout-compose:1.0.1")
                api("androidx.appcompat:appcompat:1.5.1")
                api("androidx.core:core-ktx:1.9.0")
            }
        }
        val iosArm64Main by getting {
            dependsOn(sqlDriverNativeMain)
            dependencies {
                implementation("io.ktor:ktor-client-darwin:$ktor_version")
                implementation("com.squareup.sqldelight:native-driver:1.5.3")
            }
        }
        val iosSimulatorArm64Main by getting {
            dependsOn(sqlDriverNativeMain)

            dependencies {
                implementation("io.ktor:ktor-client-darwin:$ktor_version")
                implementation("com.squareup.sqldelight:native-driver:1.5.3")
            }
        }
        val iosX64Main by getting {
            dependsOn(sqlDriverNativeMain)

            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-iosx64:1.6.4")
                implementation("io.ktor:ktor-client-darwin:$ktor_version")
                implementation("com.squareup.sqldelight:native-driver:1.5.3")
            }
        }


        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }
        val jvmMain by getting {
            kotlin.srcDirs(
                projectDir.resolve("build/generated/source/kmp-grpc/jvmMain/kotlin").canonicalPath,
            )
            dependencies {
                implementation(Deps.Kotlinx.coroutines)
                implementation(Deps.Kotlinx.JVM.coroutinesSwing)
                implementation(Deps.SqlDelight.jvmDriver)
                api(project(":generate-proto"))
                api("io.github.timortel:grpc-multiplatform-lib-jvm:0.2.2")
                implementation("io.ktor:ktor-client-java:$ktor_version")
                implementation("com.alialbaali.kamel:kamel-image:0.4.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4")
                api(compose.preview)
                implementation(Deps.Koin.core_jvm)
            }
        }
    }
}

grpcKotlinMultiplatform {
    targetSourcesMap.put(OutputTarget.COMMON, listOf(kotlin.sourceSets.getByName("commonMain")))
    targetSourcesMap.put(OutputTarget.JVM, listOf(kotlin.sourceSets.getByName("jvmMain")))
    targetSourcesMap.put(OutputTarget.Android, listOf(kotlin.sourceSets.getByName("androidMain")))
    targetSourcesMap.put(
        OutputTarget.IOS,
        listOf(
            kotlin.sourceSets.getByName("iosArm64Main"),
            kotlin.sourceSets.getByName("iosSimulatorArm64Main"),
            kotlin.sourceSets.getByName("iosX64Main")
        )
    )
    //Specify the folders where your proto files are located, you can list multiple.
    protoSourceFolders.set(listOf(projectDir.parentFile.resolve("protos/src/main/proto")))
}

kotlin {
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.all {
            // TODO: the current compose binary surprises LLVM, so disable checks for now.
            freeCompilerArgs += "-Xdisable-phases=VerifyBitcode"
        }
    }
}

sqldelight {
    database("SlackDB") {
        packageName = "dev.baseio.database"
        linkSqlite = true
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

kotlin {
    targets.withType<KotlinNativeTarget> {
        binaries.all {
            // TODO: the current compose binary surprises LLVM, so disable checks for now.
            freeCompilerArgs += "-Xdisable-phases=VerifyBitcode"
        }
    }
}

