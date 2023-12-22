import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
    alias(libs.plugins.compose)
}

group = "dev.baseio.slackclone.desktop"
version = "1.0"

tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs = listOf("-Xcontext-receivers")
    kotlinOptions.jvmTarget = "11"
}


kotlin {
    jvm {
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(libs.ktor.jvm)
                implementation(libs.ktor.core)
                implementation(libs.ktor.cio)
                implementation(libs.grpc.netty)
                implementation(project(":commoncomposeui"))
                api(project(":common"))
                implementation(compose.desktop.currentOs)
                implementation(libs.windows.registry.util)
                api(libs.decompose.core)
                api(libs.decompose.core.jvm)
                api(project(":slack_data_layer"))
                api(project(":slack_domain_layer"))
                implementation(libs.koin.core)
                implementation(libs.koin.core.jvm)
            }
        }
    }
}

val macExtraPlistKeys: String
    get() = """
      <key>CFBundleURLTypes</key>
      <array>
        <dict>
          <key>CFBundleURLName</key>
          <string>dev.baseio.slackclone</string>
          <key>CFBundleURLSchemes</key>
          <array>
            <string>slackclone</string>
          </array>
        </dict>
      </array>
    """

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            modules("java.sql")
            targetFormats(TargetFormat.Dmg)
            version = "1.0.0"
            description = "Slack Clone"
            copyright = "© 2022 Anmol Verma. All rights reserved."
            vendor = "Anmol Verma"

            macOS {
                packageName = "Slack Clone"
                dockName = "Slack Clone"
                bundleID = "dev.baseio.slackclone"
                iconFile.set(project.file("icon.png"))
                infoPlist {
                    extraKeysRawXml = macExtraPlistKeys
                }
            }
        }
    }
}
