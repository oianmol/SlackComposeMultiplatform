import org.jetbrains.compose.experimental.dsl.IOSDevices
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin(BuildPlugins.MULTIPLATFORM)
    id(BuildPlugins.SQLDELIGHT_ID)
    id(BuildPlugins.COMPOSE_ID) version Lib.AndroidX.COMPOSE_VERSION
}

version = "1.0.0"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

kotlin {
    listOf(iosX64("uikitX64"), iosArm64("uikitArm64")).forEach {
        it.binaries {
            executable {
                entryPoint = "main"
                freeCompilerArgs += listOf(
                    "-linker-option", "-framework", "-linker-option", "Metal",
                    "-linker-option", "-framework", "-linker-option", "CoreText",
                    "-linker-option", "-framework", "-linker-option", "CoreGraphics"
                )
            }
        }
    }

    sourceSets {
        val uikitMain by creating {
            dependencies {
                implementation(project(Lib.Project.common))
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.runtime)
                implementation(Lib.Networking.KTOR_DARWIN)
                implementation(Lib.Persistence.SQLDELIGHT_NATIVE)
                implementation(Lib.Decompose.core)
                api(Lib.Project.SLACK_DATA_COMMON)
                api(Lib.Project.SLACK_DOMAIN_COMMON)
            }
        }
        val uikitX64Main by getting {
            dependsOn(uikitMain)
            dependencies {
                api(Lib.Project.SLACK_DATA_IOSX64)
                api(Lib.Project.SLACK_DOMAIN_IOSX64)
            }
        }
        val uikitArm64Main by getting {
            dependsOn(uikitMain)
            dependencies {
                api(Lib.Project.SLACK_DATA_IOSARM64)
                api(Lib.Project.SLACK_DOMAIN_IOSARM64)
            }
        }
    }
}

compose.experimental {
    uikit.application {
        bundleIdPrefix = "dev.baseio"
        projectName = "SlackComposeClone"
        deployConfigurations {
            simulator("IPhone13Pro") {
                // Usage: ./gradlew iosDeployIPhone8Debug
                device = IOSDevices.IPHONE_13_PRO
            }
            simulator("IPad") {
                // Usage: ./gradlew iosDeployIPadDebug
                device = IOSDevices.IPAD_MINI_6th_Gen
            }
            connectedDevice("Device") {
                // Usage: ./gradlew iosDeployDeviceRelease
                this.teamId = "***"
            }
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

kotlin {
    targets.withType<KotlinNativeTarget> {
        binaries.all {
            // TODO: the current compose binary surprises LLVM, so disable checks for now.
            freeCompilerArgs += "-Xdisable-phases=VerifyBitcode"
        }
    }
}
