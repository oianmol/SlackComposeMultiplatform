import com.google.protobuf.gradle.*

plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.7.20"

    id("java")
    id("com.google.protobuf") version "0.8.19"
}

repositories {
    gradlePluginPortal()
    google()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.20") {
        exclude("org.antlr")
    }
    implementation("com.android.tools.build:gradle:7.2.2") {
        exclude("org.antlr")
    }

    implementation("com.google.protobuf:protobuf-kotlin:3.21.9")
    implementation("com.google.protobuf:protobuf-java-util:3.21.8")
    implementation("io.grpc:grpc-protobuf:1.50.1")
    implementation("io.grpc:grpc-stub:1.50.2")
    implementation("io.grpc:grpc-kotlin-stub:1.3.0")
    implementation("io.grpc:grpc-netty-shaded:1.50.2")
    implementation("com.google.guava:guava:31.1-jre")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}

sourceSets {
    main {
        proto {
            srcDirs("../slack_protos/src/main/proto")
        }
        kotlin.srcDir(buildDir.resolve("generated/source/proto/main/grpc"))
        kotlin.srcDir(buildDir.resolve("generated/source/proto/main/grpckt"))
        kotlin.srcDir(buildDir.resolve("generated/source/proto/main/java"))
        kotlin.srcDir(buildDir.resolve("generated/source/proto/main/kotlin"))
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.21.1"
    }

    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.48.1"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.2.1:jdk7@jar"
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
                    option("lite")
                }
            }
        }
    }
}



tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}