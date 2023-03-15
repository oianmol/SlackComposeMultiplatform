import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
    alias(libs.plugins.compose)
}

group = "dev.baseio.slackclone.desktop"
version = "1.0"

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
                implementation(libs.kamelimage)
                implementation(libs.ktor.jvm)
                implementation(libs.ktor.core)
                implementation(libs.ktor.cio)
                implementation(libs.grpc.netty)
                implementation(project(":commoncomposeui"))
                api(project(":common"))
                implementation(compose.desktop.currentOs)
                implementation("com.github.sarxos:windows-registry-util:0.3")
                api(libs.decompose.core)
                api(libs.decompose.core.jvm)
                api(project(":slack_data_layer"))
                api(project(":slack_domain_layer"))
                implementation(libs.koin.core)
                implementation(libs.koin.core.jvm)
            }
        }
        val jvmTest by getting{
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            dependencies{
                implementation(compose.uiTestJUnit4)
                implementation(libs.koin.test)
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
                packageName  = "Slack Clone"
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
