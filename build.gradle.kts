@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.navigation.safeargs.kotlin) apply false
}

group = "dev.baseio.slackclone"
version = "1.0"

subprojects {
    apply(plugin = rootProject.libs.plugins.ktlint.get().pluginId)

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        disabledRules.set(setOf("no-wildcard-imports"))
        filter {
            exclude { entry ->
                entry.file.toString().contains("generated")
            }
        }
    }
}

apply(from = teamPropsFile("git-hooks.gradle.kts"))

fun teamPropsFile(propsFile: String): File {
    val teamPropsDir = file("team-props")
    return File(teamPropsDir, propsFile)
}
