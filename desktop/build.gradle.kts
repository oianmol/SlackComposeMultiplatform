import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
  kotlin("multiplatform")
  id("org.jetbrains.compose") version "1.2.0"
}

group = "dev.baseio.slackclone"
version = "1.0"

object Decompose {
  val VERSION = "1.0.0-alpha-06"
  val core = "com.arkivanov.decompose:decompose:${VERSION}"
  val coreJvm = "com.arkivanov.decompose:decompose-jvm:${VERSION}"
}

kotlin {
  jvm {
    compilations.all {
      kotlinOptions.jvmTarget = "11"
    }
    withJava()
  }
  sourceSets {
    val jvmMain by getting {
      dependencies {
        implementation("io.grpc:grpc-netty-shaded:1.49.2")
        implementation(project(":common"))
        implementation(compose.desktop.currentOs)
        api(Decompose.core)
        api(Decompose.coreJvm)
        api("dev.baseio.slackclone:slack_kmp_data-jvm:1.0")
      }
    }
    val jvmTest by getting
  }
}

compose.desktop {
  application {
    mainClass = "MainKt"
    nativeDistributions {
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
      modules("java.sql")
      packageName = "jvm"
      packageVersion = "1.0.0"
    }
  }
}