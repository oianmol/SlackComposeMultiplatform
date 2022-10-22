plugins {
  id("org.jetbrains.compose") version "1.2.0"
  id("com.android.application")
  kotlin("android")
}

group = "dev.baseio.slackclone"
version = "1.0"

repositories {
  jcenter()
  mavenLocal()
}

object Decompose {
  val VERSION = "1.0.0-alpha-06"
  val core = "com.arkivanov.decompose:decompose:${VERSION}"
}

dependencies {
  implementation(project(":common"))
  implementation("androidx.activity:activity-compose:1.6.0")
  implementation("androidx.core:core-splashscreen:1.0.0")
  implementation("io.grpc:grpc-okhttp:1.49.2")
  api("dev.baseio.slackclone:slack_kmp_data-android:1.0")
  api(Decompose.core)
  // Test rules and transitive dependencies:
  androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.2.1")
  // Needed for createComposeRule, but not createAndroidComposeRule:
  debugImplementation("androidx.compose.ui:ui-test-manifest:1.2.1")
}

android {
  compileSdk = 33
  packagingOptions {
    resources.excludes.add("google/protobuf/*.proto")
  }
  defaultConfig {
    versionCode = 1
    versionName = "1.0"
    applicationId = "dev.baseio.slackclone"
    minSdk = 24
    targetSdk = 33
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
    }
  }
}