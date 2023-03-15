import io.github.timortel.kotlin_multiplatform_grpc_plugin.GrpcMultiplatformExtension.OutputTarget

plugins {
    id("maven-publish")
    id(libs.plugins.kotlin.native.cocoapods.get().pluginId)
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.rick.nativecoroutines)
    alias(libs.plugins.sqldelight.id)
    alias(libs.plugins.google.protobuf)
    alias(libs.plugins.timortel.grpc)

}

group = "dev.baseio.slackclone"
version = "1.0"


val ktor_version = "2.1.0"

object Jvm {
    val target = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
    mavenLocal()
    google()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    android() {
        publishLibraryVariants("release")
    }

    cocoapods {
        summary = "Slack Data Library"
        homepage = "https://github.com/oianmol"
        ios.deploymentTarget = "14.1"
        framework {
            baseName = "slack_data_layer"
            isStatic = true
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
                implementation("com.squareup.sqldelight:runtime:1.5.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
                implementation(libs.datetime)
                implementation(libs.sqldelight.runtime)
                implementation(libs.coroutines)
                implementation(project(":slack_domain_layer"))
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
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            kotlin.srcDirs(
                projectDir.resolve("build/generated/source/kmp-grpc/jvmMain/kotlin").canonicalPath,
            )
            dependencies {
                implementation(libs.sqldelight.androiddriver)
                implementation("androidx.security:security-crypto-ktx:1.1.0-alpha05")
                api(project(":slack_generate_protos"))
                implementation("io.ktor:ktor-client-android:$ktor_version")
            }
        }
        val iosArm64Main by getting {
            dependsOn(sqlDriverNativeMain)
            dependencies {
                implementation(libs.coroutinesarm64)
            }
        }
        val iosSimulatorArm64Main by getting {
            dependsOn(sqlDriverNativeMain)
            dependencies {
                implementation(libs.coroutinesiossimulatorarm64)
            }
        }
        val iosX64Main by getting {
            dependsOn(sqlDriverNativeMain)
            dependencies {
                implementation(libs.coroutinesx64)
            }
        }

        val iosMain by creating {
            kotlin.srcDirs(
                projectDir.resolve("build/generated/source/kmp-grpc/iosMain/kotlin").canonicalPath,
            )
            dependencies {
                implementation("com.squareup.sqldelight:native-driver:1.5.3")
                implementation("io.ktor:ktor-client-darwin:$ktor_version")
            }
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }


        val androidUnitTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }
        val jvmMain by getting {
            kotlin.srcDirs(
                projectDir.resolve("build/generated/source/kmp-grpc/jvmMain/kotlin").canonicalPath,
            )
            dependencies {
                implementation(libs.coroutines.swing)
                implementation(libs.sqldelight.jvmdriver)
                api(project(":slack_generate_protos"))
                implementation("io.ktor:ktor-client-java:$ktor_version")
            }
        }
    }
}

grpcKotlinMultiplatform {
    targetSourcesMap.put(OutputTarget.COMMON, listOf(kotlin.sourceSets.getByName("commonMain")))
    targetSourcesMap.put(
        OutputTarget.JVM,
        listOf(kotlin.sourceSets.getByName("jvmMain"), kotlin.sourceSets.getByName("androidMain"))
    )
    targetSourcesMap.put(
        OutputTarget.IOS,
        listOf(
            kotlin.sourceSets.getByName("iosArm64Main"),
            kotlin.sourceSets.getByName("iosSimulatorArm64Main"),
            kotlin.sourceSets.getByName("iosX64Main")
        )
    )
    //Specify the folders where your proto files are located, you can list multiple.
    protoSourceFolders.set(listOf(projectDir.parentFile.resolve("slack_protos/src/main/proto")))
}

dependencies {
    commonMainApi(project(":capillary-kmp"))
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
        minSdk = (24)
        targetSdk = (33)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

