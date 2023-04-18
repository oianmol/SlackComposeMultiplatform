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
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
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
                implementation("org.bouncycastle:bcprov-jdk16:1.46")
            }
        }
        val jvmTest by getting
        val androidMain by getting {
            dependencies {
                implementation("joda-time:joda-time:2.9.9")
                api("com.google.firebase:firebase-core:21.1.1")
                api("com.google.firebase:firebase-messaging:23.1.2")
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
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
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }
    namespace = "org.example.library"
}
dependencies {
    androidTestImplementation("junit:junit:4.12")
    // Core library
    androidTestImplementation("androidx.test:core:1.5.0")

    // AndroidJUnitRunner and JUnit Rules
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")

    // Assertions
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.ext:truth:1.5.0")

}
