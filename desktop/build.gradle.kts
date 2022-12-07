import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin(BuildPlugins.MULTIPLATFORM)
    id(BuildPlugins.COMPOSE_ID) version Lib.AndroidX.COMPOSE_VERSION
}

group = ProjectProperties.APPLICATION_ID
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
                implementation(Lib.Multiplatform.kamelImage)
                implementation(Lib.Networking.KTOR_JVM)
                implementation(Lib.Networking.ktor_core)
                implementation(Lib.Networking.ktor_cio)
                implementation(Lib.Grpc.NETTY)
                implementation(project(Lib.Project.commonComposeUI))
                implementation(project(Lib.Project.common))
                implementation(compose.desktop.currentOs)
                implementation("com.github.sarxos:windows-registry-util:0.3")
                api(Lib.Decompose.core)
                api(Lib.Decompose.coreJvm)
                api(project(Lib.Project.SLACK_DATA_COMMON))
                api(project(Lib.Project.SLACK_DOMAIN_COMMON))
                implementation(Deps.Koin.core)
                implementation(Deps.Koin.core_jvm)
            }
        }
        val jvmTest by getting{
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            dependencies{
                implementation(compose.uiTestJUnit4)
                implementation(Deps.Koin.test)
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
