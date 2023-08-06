import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import java.net.InetAddress
import java.util.Enumeration
import java.net.NetworkInterface

plugins {
    id(libs.plugins.kotlin.native.cocoapods.get().pluginId)
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.rick.nativecoroutines)
    alias(libs.plugins.buildkonfig)
}

group = "dev.baseio.slackclone.common"
version = "1.0"

repositories {
    mavenCentral()
}

buildkonfig {
    packageName = "dev.baseio.slackclone"

    defaultConfigs {
        buildConfigField(STRING, "ipAddr", ipv4())
    }
}

dependencies {
    commonMainApi(libs.mokopaging)
    commonMainApi(project(":slack_domain_layer"))
    commonMainApi(project(":slack_data_layer"))
}

tasks.getByName("build").dependsOn("generateBuildKonfig")

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

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.datetime)
                implementation(libs.sqldelight.runtime)
                implementation(libs.koin.core)
                implementation(libs.coroutines)
                implementation(kotlin("stdlib-common"))
                implementation(libs.decompose.core)
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
                implementation(libs.coroutines.android)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.coroutines)
                implementation(libs.coroutines.swing)
                implementation(libs.ktor.jvm)
                implementation(libs.koin.core.jvm)
                api(libs.protobuf.java)
                api(libs.zxing.core)
                api(libs.zxing.javase)
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
        val jvmTest by getting {
            dependencies {
                implementation(libs.grpc.okhttp)
                implementation(kotlin("test-junit"))
                implementation(libs.junit)
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
    compileSdk = 34
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
    kotlin {
        jvmToolchain(11)
    }
    namespace = "dev.baseio.slackcommon"
}

fun ipv4(): String? {
    var result: InetAddress? = null
    val interfaces: Enumeration<java.net.NetworkInterface> = (NetworkInterface.getNetworkInterfaces())
    while (interfaces.hasMoreElements()) {
        val addresses: Enumeration<InetAddress> = interfaces.nextElement().getInetAddresses()
        while (addresses.hasMoreElements()) {
            val address = addresses.nextElement()
            if (!address.isLoopbackAddress) {
                if (address.isSiteLocalAddress) {
                    return address.hostAddress
                } else if (result == null) {
                    result = address
                }
            }
        }
    }
    return (result ?: InetAddress.getLocalHost()).hostAddress
}