import com.google.protobuf.gradle.*

plugins {
    kotlin("jvm")
    id("java")
    id("com.google.protobuf") version "0.9.4"
}

group = "dev.baseio.slackdatalib"
version = "1.0.0"

object Versions {
    const val GRPC = "1.62.2"
    const val GRPC_KOTLIN = "1.4.1"
    const val PROTOBUF = "3.25.3"

    const val COROUTINES = "1.7.3"
}

repositories {
    mavenCentral()
}

dependencies {
    protobuf(project(":slack_protos"))
    api(libs.protobuf.kotlin)
    api(libs.protobuf.java.util)
    api(libs.grpc.protobuf)
    api(libs.grpc.stub)
    api(libs.grpc.kotlin.stub)
    api(libs.kotlinx.coroutines.core)
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${Versions.PROTOBUF}"
    }

    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${Versions.GRPC}"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:${Versions.GRPC_KOTLIN}:jdk8@jar"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc") {
                }
                id("grpckt") {
                }
            }

            it.builtins {
                id("kotlin") {
                }
            }
        }
    }
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
