import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id(libs.plugins.kotlin.native.cocoapods.get().pluginId)
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.rick.nativecoroutines)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.symbol.processing)
    id("io.github.takahirom.roborazzi")
}

group = "dev.baseio.slackclone.composeui"
version = "1.0"

tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs = listOf("-Xcontext-receivers")
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
                api(libs.mokopaging)
                api(project(":slack_domain_layer"))
                api(project(":slack_data_layer"))
                implementation(project(":common"))
                implementation(libs.datetime)
                implementation(libs.sqldelight.runtime)
                implementation(libs.koin.core)
                implementation(libs.kamel.image)

                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.materialIconsExtended)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)

                implementation(libs.coroutines)
                implementation(kotlin("stdlib-common"))
                implementation(libs.decompose.core)
                implementation(libs.decompose.composejb)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.koin.test)
                implementation(kotlin("test"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(libs.coroutines.test)
                implementation(libs.turbine)
                implementation(libs.test.core)
                implementation(libs.mockative)
            }
        }

        val androidUnitTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(libs.junit)
                implementation(libs.sqldelight.jvmdriver)
                implementation(libs.sqldelight.androiddriver)
                implementation(libs.androidx.junit.ext.ktx)
                implementation(libs.coroutines.test)
                implementation(libs.grpc.okhttp)
            }
        }
        val androidInstrumentedTest by getting {
            dependencies {
                implementation(libs.androidx.core)
                implementation(libs.androidx.ui.test.manifest)
                implementation(libs.androidx.ui.test)
                implementation(libs.androidx.ui.test.junit4)
                implementation(libs.sqldelight.jvmdriver)
                implementation(libs.sqldelight.androiddriver)
                implementation(libs.coroutines.test)
                implementation(libs.coroutines)
                implementation(libs.androidx.uiautomator)
                implementation(libs.androidx.rules)

                implementation(libs.koin.test)
                implementation(kotlin("test"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(libs.test.core)
                implementation(libs.mockative)

            }
        }
        val jvmTest by getting {
            @OptIn(ExperimentalComposeLibrary::class)
            dependencies {
                implementation(libs.grpc.okhttp)
                implementation(kotlin("test-junit"))
                implementation(libs.junit)
                implementation(libs.sqldelight.jvmdriver)
                implementation(libs.coroutines.test)
                implementation(libs.koin.test)
                implementation(libs.mockative)
                implementation(compose.uiTestJUnit4)
            }
        }
        val androidMain by getting {
            dependencies {
                // CameraX
                api(libs.androidx.camera.camera2)
                api(libs.androidx.camera.lifecycle)
                api(libs.androidx.camera.view)
                api(libs.androidx.camera.video)
                api(libs.androidx.camera.extensions)
                implementation(libs.guava)
                // Zxing
                api(libs.zxing.core)

                implementation(libs.barcode.scanning)
                api(libs.activity.compose)
                api(libs.androidx.lifecycle.runtime.ktx)
                implementation(libs.firebase.core)
                implementation(libs.firebase.messaging)
                implementation(libs.firebase.messaging.ktx)
                implementation(libs.koin.android)
                implementation(libs.coroutines)
                implementation(libs.lifecycleviewmodelktx)
                implementation(libs.securitycrypto)
                implementation(libs.accompanist.system.ui.controller)
                implementation(libs.coroutines.android)
                implementation(libs.coil.compose)
                implementation(libs.decompose.composejb)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.coroutines)
                implementation(libs.coroutines.swing)
                api(libs.ktor.jvm)
                api(compose.preview)
                implementation(libs.koin.core.jvm)
                implementation(libs.decompose.composejb)
                api(libs.protobuf.java)
            }
        }
        val iosX64Main by getting {
        }
        val iosArm64Main by getting {
        }
        val iosSimulatorArm64Main by getting {
        }

        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.decompose.composejb)
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

dependencies {
    configurations
        .filter { it.name.startsWith("ksp") && it.name.contains("Test") }
        .forEach {
            add(it.name, "io.mockative:mockative-processor:1.4.1")
        }
}

android {
    compileSdk = 34

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = 24
        targetSdk = 33
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
    packagingOptions {
        resources.excludes.addAll(listOf("META-INF/INDEX.LIST", "google/protobuf/*.proto"))
    }
    namespace = "dev.baseio.composeui"
    kotlin {
        jvmToolchain(11)
    }
}
