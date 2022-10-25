import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    kotlin(BuildPlugins.MULTIPLATFORM)
    id(BuildPlugins.ANDROID_LIBRARY_PLUGIN)
    id(BuildPlugins.KOTLIN_PARCELABLE_PLUGIN)
    id(BuildPlugins.COMPOSE_ID) version Lib.AndroidX.COMPOSE_VERSION
    kotlin(BuildPlugins.SERIALIZATION) version Lib.Kotlin.KOTLIN_VERSION
}

group = ProjectProperties.APPLICATION_ID
version = "1.0"


repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    commonMainApi(Lib.Multiplatform.mokoPaging)
}

kotlin {
    val iosEnabled = true
    targets(iosEnabled)

    sourceSets {
        commonDependencies(this@kotlin)
        androidDependencies(this@kotlin)
        jvmDependencies(this@kotlin)
        if (iosEnabled) {
            iosArmDependencies()
            iosSimulatorArmDependencies()
            iosX64Dependencies()
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
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 24
        targetSdk = 33
    }
    packagingOptions {
        resources.excludes.add("google/protobuf/*.proto")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

fun KotlinMultiplatformExtension.targets(iosEnabled: Boolean = true) {
    android()
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    if (iosEnabled) {
        iosArm64()
        iosSimulatorArm64()
        iosX64()
    }
}

fun NamedDomainObjectContainer<org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet>.commonDependencies(
    kotlinMultiplatformExtension: KotlinMultiplatformExtension
) {
    val commonMain by getting {
        dependencies {
            implementation(Lib.Project.SLACK_DOMAIN_COMMON)
            implementation(Lib.Project.SLACK_DATA_COMMON)
            implementation(Deps.Kotlinx.datetime)
            implementation(Deps.SqlDelight.runtime)
            implementation(Deps.Koin.core)
            api(kotlinMultiplatformExtension.compose.runtime)
            api(kotlinMultiplatformExtension.compose.foundation)
            api(kotlinMultiplatformExtension.compose.material)
            implementation(Deps.Kotlinx.datetime)
            implementation(Deps.SqlDelight.runtime)
            implementation(Lib.Async.COROUTINES)
            implementation(Deps.Koin.core)
            implementation(kotlin("stdlib-common"))
            implementation(Lib.Decompose.core)
            implementation(Lib.Decompose.composejb)
        }
    }
    val commonTest by getting {
        dependencies {
            implementation(Deps.Koin.test)
            implementation(kotlin("test"))
        }
    }
}

fun NamedDomainObjectContainer<org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet>.androidDependencies(
    kotlinMultiplatformExtension: KotlinMultiplatformExtension
) {
    val androidMain by getting {
        dependencies {
            implementation(Lib.Project.SLACK_DOMAIN_ANDROID)
            implementation(Lib.Project.SLACK_DATA_ANDROID)
            implementation(Deps.Koin.android)
            implementation(Lib.Async.COROUTINES)
            implementation(Deps.AndroidX.lifecycleViewModelKtx)
            implementation(Lib.AndroidX.securityCrypto)
            implementation(Lib.AndroidX.ACCOMPANIST_SYSTEM_UI_CONTROLLER)
            implementation(Lib.Async.COROUTINES_ANDROID)
            implementation(Lib.AndroidX.COIL_COMPOSE)
            implementation(Lib.Decompose.composejb)
        }
    }
    val androidTest by getting {
        dependencies {
            implementation("junit:junit:4.13.2")
        }
    }
}

fun NamedDomainObjectContainer<org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet>.iosArmDependencies() {
    val iosArm64Main by getting {
        dependencies {
            implementation(Lib.Project.SLACK_DOMAIN_IOSARM64)
            implementation(Lib.Project.SLACK_DATA_IOSARM64)
            implementation(Lib.Decompose.composejb)
        }
    }
}

fun NamedDomainObjectContainer<org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet>.iosSimulatorArmDependencies(){
    val iosSimulatorArm64Main by getting {
        dependencies {
            implementation(Lib.Project.SLACK_DOMAIN_IOSSIMULATORARM64)
            implementation(Lib.Project.SLACK_DATA_IOSSIMULATORARM64)
            implementation(Lib.Decompose.composejb)
        }
    }
}


fun NamedDomainObjectContainer<org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet>.iosX64Dependencies() {
    val iosX64Main by getting {
        dependencies {
            implementation(Lib.Project.SLACK_DOMAIN_IOSX64)
            implementation(Lib.Project.SLACK_DATA_IOSX64)
            implementation(Lib.Decompose.composejb)
        }
    }
}

fun NamedDomainObjectContainer<org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet>.jvmDependencies(
    kotlinMultiplatformExtension: KotlinMultiplatformExtension
) {
    val jvmMain by getting {
        dependencies {
            implementation(Lib.Project.SLACK_DOMAIN_JVM)
            implementation(Lib.Project.SLACK_DATA_JVM)
            implementation(Lib.Async.COROUTINES)
            implementation(Deps.Kotlinx.JVM.coroutinesSwing)
            implementation("io.ktor:ktor-client-java:2.1.0")
            implementation(Lib.Multiplatform.kamelImage)
            api(kotlinMultiplatformExtension.compose.preview)
            implementation(Deps.Koin.core_jvm)
            implementation(Lib.Decompose.composejb)
        }
    }
}
