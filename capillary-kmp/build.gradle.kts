import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithSimulatorTests

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.kotlin.native.cocoapods)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    jvm()

    js {
        useCommonJs()

        browser {
            webpackTask {
                output.libraryTarget = "commonjs2"
            }
        }

        binaries.executable()
    }

    cocoapods {
        summary = "SlackCapillary"
        homepage = "https://github.com/oianmol"
        ios.deploymentTarget = "14.1"
        version = "1.0.0"
        pod("capillaryslack") {
            source = path(rootProject.projectDir.absolutePath + "/slack_capillary_ios/").also {
                println(it)
            }
            extraOpts += listOf("-compiler-option", "-fmodules")
        }

        framework {
            baseName = "capillaryslack"
            isStatic = true
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    applyDefaultHierarchyTemplate()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "CapillaryKmp"
            isStatic = true
        }

        val platform = if (it.targetName == "iosArm64") "iphoneos" else "iphonesimulator"

        if (it is KotlinNativeTargetWithSimulatorTests) {
            it.testRuns.forEach { tr ->
                tr.deviceId = properties["iosSimulatorName"] as? String ?: "iPhone 14"
            }
        }

        it.binaries.all {
            linkerOpts("-ObjC")
            // linkerOpts(opts)
            linkerOpts("-L/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/lib/swift/$platform")
            linkerOpts("-L/usr/lib/swift/")
        }

        it.binaries.getTest(org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType.DEBUG).apply {
            linkerOpts(
                "-rpath",
                "/usr/lib/swift"
            )
        }
    }

    targets.withType<KotlinNativeTarget>().configureEach {
        compilations.configureEach {
            compilerOptions.configure {
                freeCompilerArgs.addAll(
                    "-opt-in=kotlinx.cinterop.ExperimentalForeignApi",
                    "-opt-in=kotlinx.cinterop.BetaInteropApi"
                )
            }
        }
    }

    sourceSets {
        all {
            languageSettings {
                optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
            }
        }
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.koin.core)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.joda.time)
            api(libs.firebase.core)
            api(libs.firebase.messaging)
        }

        jvmMain.dependencies {
            implementation(libs.kotlinx.coroutines.swing)
        }

        jsMain.dependencies {
            api(npm("node-forge", "^1.3.1"))
        }

        iosMain.dependencies {
        }

    }
}

android {
    compileSdk = (34)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = (24)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    namespace = "org.example.library"
}


afterEvaluate {
    rootProject.extensions.configure<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension> {
        versions.webpackCli.version = "4.10.0"
    }
}