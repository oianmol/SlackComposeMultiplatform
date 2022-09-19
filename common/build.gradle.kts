import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
  kotlin("multiplatform")
  id("org.jetbrains.compose") version "1.2.0-alpha01-dev774"
  id("com.android.library")
  id("com.squareup.sqldelight")
}

group = "dev.baseio.slackclone"
version = "1.0"

val ktor_version = "2.1.0"

object Versions {
  const val koin = "3.1.4"
}

object Deps {

  object Koin {
    const val core = "io.insert-koin:koin-core:${Versions.koin}"
    const val core_jvm = "io.insert-koin:koin-core-jvm:${Versions.koin}"
    const val test = "io.insert-koin:koin-test:${Versions.koin}"
    const val android = "io.insert-koin:koin-android:${Versions.koin}"
  }

}

repositories {
  mavenCentral()
  mavenLocal()
}
val slackDataVersion: String by project


kotlin {
  android()
  iosArm64()
  iosSimulatorArm64()
  iosX64()
  jvm("desktop") {
    compilations.all {
      kotlinOptions.jvmTarget = "11"
    }
  }
  sourceSets {

    val commonMain by getting {
      dependencies {
        implementation("dev.baseio.slackclone:slackdata:${slackDataVersion}")
        implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
        implementation("com.squareup.sqldelight:runtime:1.5.3")
        implementation(Deps.Koin.core)
        api(compose.runtime)
        api(compose.foundation)
        api(compose.material)
      }
    }
    val commonTest by getting {
      dependencies {
        implementation(Deps.Koin.test)
        implementation(kotlin("test"))
      }
    }
    val androidMain by getting {
      dependencies {
        implementation(Deps.Koin.android)
        implementation("com.google.accompanist:accompanist-systemuicontroller:0.26.3-beta")
        implementation("dev.baseio.slackclone:slackdata-android:${slackDataVersion}")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
        implementation("com.squareup.sqldelight:android-driver:1.5.3")
        implementation("io.coil-kt:coil-compose:2.2.0")
        implementation("io.ktor:ktor-client-android:$ktor_version")
        api("androidx.constraintlayout:constraintlayout-compose:1.0.1")
        api("androidx.appcompat:appcompat:1.5.1")
        api("androidx.core:core-ktx:1.9.0")
      }
    }
    val iosArm64Main by getting {
      dependencies {
        implementation("io.ktor:ktor-client-darwin:$ktor_version")
        implementation("dev.baseio.slackclone:slackdata-iosarm64:${slackDataVersion}")
      }
    }
    val iosSimulatorArm64Main by getting {
      dependencies {
        implementation("io.ktor:ktor-client-darwin:$ktor_version")
        implementation("dev.baseio.slackclone:slackdata-iossimulatorarm64:${slackDataVersion}")
      }
    }
    val iosX64Main by getting {
      dependencies {
        implementation("io.ktor:ktor-client-darwin:$ktor_version")
        implementation("dev.baseio.slackclone:slackdata-iosx64:${slackDataVersion}")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-iosx64:1.6.4")
      }
    }

    val androidTest by getting {
      dependencies {
        implementation("junit:junit:4.13.2")
      }
    }
    val desktopMain by getting {
      dependencies {
        implementation("dev.baseio.slackclone:slackdata-jvm:${slackDataVersion}")
        implementation("io.ktor:ktor-client-java:$ktor_version")
        implementation("com.alialbaali.kamel:kamel-image:0.4.0")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4")
        api(compose.preview)
        implementation(Deps.Koin.core_jvm)
      }
    }
    val desktopTest by getting
  }
}

android {
  compileSdk = 33
  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  defaultConfig {
    minSdk = 24
    targetSdk = 33
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
}

kotlin {
  targets.withType<KotlinNativeTarget> {
    binaries.all {
      // TODO: the current compose binary surprises LLVM, so disable checks for now.
      freeCompilerArgs += "-Xdisable-phases=VerifyBitcode"
    }
  }
}