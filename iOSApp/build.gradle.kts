import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.compose.experimental.dsl.IOSDevices

plugins {
  kotlin("multiplatform")
  id("com.squareup.sqldelight")
  id("org.jetbrains.compose") version "1.2.0-alpha01-dev755"
}

version = "1.0.0"
val ktor_version = "2.1.0"

repositories {
  mavenLocal()
  mavenCentral()
  maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  google()
}


kotlin {
  iosX64("uikitX64") {
    binaries {
      executable() {
        entryPoint = "main"
        freeCompilerArgs += listOf(
          "-linker-option", "-framework", "-linker-option", "Metal",
          "-linker-option", "-framework", "-linker-option", "CoreText",
          "-linker-option", "-framework", "-linker-option", "CoreGraphics"
        )
      }
    }
  }
  iosArm64("uikitArm64") {
    binaries {
      executable() {
        entryPoint = "main"
        freeCompilerArgs += listOf(
          "-linker-option", "-framework", "-linker-option", "Metal",
          "-linker-option", "-framework", "-linker-option", "CoreText",
          "-linker-option", "-framework", "-linker-option", "CoreGraphics"
        )
        // TODO: the current compose binary surprises LLVM, so disable checks for now.
        freeCompilerArgs += "-Xdisable-phases=VerifyBitcode"
      }
    }
  }

  sourceSets {
    val uikitMain by creating {
      dependencies {
        implementation(project(":common"))
        implementation(compose.ui)
        implementation(compose.foundation)
        implementation(compose.material)
        implementation(compose.runtime)
        implementation("io.ktor:ktor-client-darwin:$ktor_version")
        implementation("com.squareup.sqldelight:native-driver:1.5.3")
      }
    }
    val uikitX64Main by getting {
      dependsOn(uikitMain)
      dependencies {
        implementation("dev.baseio.slackclone:slackdata-iosx64:1.0.0")
      }
    }
    val uikitArm64Main by getting {
      dependsOn(uikitMain)
      dependencies {
        implementation("dev.baseio.slackclone:slackdata-iosarm64:1.0.0")
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
        //Usage: ./gradlew iosDeployIPhone8Debug
        device = IOSDevices.IPHONE_13_PRO
      }
      simulator("IPad") {
        //Usage: ./gradlew iosDeployIPadDebug
        device = IOSDevices.IPAD_MINI_6th_Gen
      }
      connectedDevice("Device") {
        //Usage: ./gradlew iosDeployDeviceRelease
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