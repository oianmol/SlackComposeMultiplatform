import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.symbol.processing)
}

group = "dev.baseio.slackclone.desktop"
version = "1.0"

tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs = listOf("-Xcontext-receivers")
    kotlinOptions.jvmTarget = "11"
}

val osName = System.getProperty("os.name")
val targetOs = when {
    osName == "Mac OS X" -> "macos"
    osName.startsWith("Win") -> "windows"
    osName.startsWith("Linux") -> "linux"
    else -> error("Unsupported OS: $osName")
}

val targetArch = when (val osArch = System.getProperty("os.arch")) {
    "x86_64", "amd64" -> "x64"
    "aarch64" -> "arm64"
    else -> error("Unsupported arch: $osArch")
}

val skikoversion = "0.7.76" // or any more recent version
val target = "${targetOs}-${targetArch}"


dependencies {
    configurations
        .filter { it.name.startsWith("ksp") && it.name.contains("Test") }
        .forEach {
            add(it.name, "io.mockative:mockative-processor:1.4.1")
        }
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
                implementation(libs.windows.registry.util)
                api(libs.decompose.core)
                api(libs.decompose.core.jvm)
                api(project(":slack_data_layer"))
                api(project(":slack_domain_layer"))
                implementation(libs.koin.core)
                implementation(libs.koin.core.jvm)
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(libs.koin.test)
                implementation(libs.mockative)
                @OptIn(ExperimentalComposeLibrary::class)
                implementation(compose.uiTestJUnit4) // there is no non-ui testing
                implementation(compose.desktop.currentOs) // ui-testings needs skiko

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
