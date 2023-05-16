import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithSimulatorTests

plugins {
    id(libs.plugins.kotlin.native.cocoapods.get().pluginId)
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
    alias(libs.plugins.android.library)
}

group = "dev.baseio.slackcrypto"
version = "1.0"

repositories {
    google()
    mavenCentral()
    mavenLocal()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    android {
        publishLibraryVariants("release")
    }

    cocoapods {
        summary = "Capillary encryption Library"
        homepage = "https://github.com/oianmol"
        ios.deploymentTarget = "14.1"
        framework {
            baseName = "capillaryslack"
            isStatic = true
        }

        pod("capillaryslack") {
            source = path(rootProject.projectDir.absolutePath + "/slack_capillary_ios/")
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
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

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.coroutines)
                implementation("com.squareup.okio:okio:3.2.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.bcprov.jdk16)
            }
        }
        val jvmTest by getting
        val androidMain by getting {
            dependencies {
                implementation(libs.joda.time)
                api(libs.firebase.core)
                api(libs.firebase.messaging)
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.junit)
                implementation(kotlin("test"))
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
    }
}

android {
    compileSdk = (31)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = (24)
        testInstrumentationRunner =  "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    namespace = "org.example.library"
}
dependencies {
    androidTestImplementation(libs.junit)
    // Core library
    androidTestImplementation(libs.androidx.core)

    // AndroidJUnitRunner and JUnit Rules
    androidTestImplementation(libs.androidxrunner)
    androidTestImplementation(libs.androidx.rules)
    androidTestImplementation(libs.coroutines.test)

    // Assertions
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.truth)

}
