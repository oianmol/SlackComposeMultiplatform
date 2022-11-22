import org.jetbrains.compose.experimental.dsl.IOSDevices
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin(BuildPlugins.MULTIPLATFORM)
    id(BuildPlugins.SQLDELIGHT_ID)
    kotlin("native.cocoapods")
    id(BuildPlugins.COMPOSE_ID) version Lib.AndroidX.COMPOSE_VERSION
}

version = "1.0.0"

repositories {
    mavenCentral()
    mavenLocal()
    google()
}

kotlin {
    listOf(iosX64("uikitX64"), iosArm64("uikitArm64")).forEach {
        val platform = if (it.targetName == "iosArm64") "iphoneos" else "iphonesimulator"

        it.binaries {
            executable {
                listOf(
                    "abseil",
                    "BoringSSL-GRPC",
                    "gRPC",
                    "gRPC-Core",
                    "gRPC-ProtoRPC",
                    "gRPC-RxLibrary"
                ).forEach { name ->
                    linkerOpts("-F/Users/anmolverma/IdeaProjects/SlackComposeMultiplatform/common/build/cocoapods/synthetic/IOS/build/Release-${platform}/$name")
                    linkerOpts(
                        "-rpath",
                        "/Users/anmolverma/IdeaProjects/SlackComposeMultiplatform/common/build/cocoapods/synthetic/IOS/build/Release-${platform}/$name"
                    )
                    linkerOpts(
                        "-framework", when (name) {
                            "abseil" -> "absl"
                            "BoringSSL-GRPC" -> "openssl_grpc"
                            "gRPC" -> "GRPCClient"
                            "gRPC-Core" -> "grpc"
                            "gRPC-ProtoRPC" -> "ProtoRPC"
                            "gRPC-RxLibrary" -> "RxLibrary"
                            else -> name
                        }
                    )

                }

                entryPoint = "main"
                freeCompilerArgs += listOf(
                    "-linker-option", "-framework", "-linker-option", "Metal",
                    "-linker-option", "-framework", "-linker-option", "CoreText",
                    "-linker-option", "-framework", "-linker-option", "CoreGraphics",
                )
            }
        }
    }

    sourceSets {
        val uikitMain by creating {
            dependencies {
                implementation(project(Lib.Project.commonComposeUI))
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.runtime)
                implementation(Lib.Networking.KTOR_DARWIN)
                implementation(Lib.Persistence.SQLDELIGHT_NATIVE)
                implementation(Lib.Decompose.core)
                api(project(Lib.Project.SLACK_DATA_COMMON))
                api(project(Lib.Project.SLACK_DOMAIN_COMMON))
                implementation(Deps.Koin.core)
            }
        }
        val uikitX64Main by getting {
            dependsOn(uikitMain)
            dependencies {
            }
        }
        val uikitArm64Main by getting {
            dependsOn(uikitMain)
            dependencies {
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