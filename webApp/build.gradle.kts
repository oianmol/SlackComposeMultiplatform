import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
    alias(libs.plugins.compose)
}

val copyJsResources = tasks.create("copyJsResourcesWorkaround", Copy::class.java) {
    from(project(":commoncomposeui").file("src/commonMain/resources"))
    into("build/processedResources/js/main")
}

val copyWasmResources = tasks.create("copyWasmResourcesWorkaround", Copy::class.java) {
    from(project(":commoncomposeui").file("src/commonMain/resources"))
    into("build/processedResources/wasm/main")
}

afterEvaluate {
    project.tasks.getByName("jsProcessResources").finalizedBy(copyJsResources)
    project.tasks.getByName("wasmProcessResources").finalizedBy(copyWasmResources)
}

kotlin {
    js(IR) {
        moduleName = "slackwebapp"
        browser()
        binaries.executable()
    }

    wasm {
        moduleName = "slackwebapp"
        browser {
            commonWebpackConfig {
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).copy(
                    open = mapOf(
                        "app" to mapOf(
                            "name" to "google chrome",
                        ),
                    ),
                )
            }
        }
        binaries.executable()
    }

    sourceSets {
        val jsWasmMain by creating {
            dependencies {
                implementation(project(":commoncomposeui"))
                api(project(":common"))
                api(project(":slack_data_layer"))
                api(project(":slack_domain_layer"))
                implementation(compose.runtime)
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                api(libs.decompose.core)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
            }
        }
        val jsMain by getting {
            dependsOn(jsWasmMain)
        }
        val wasmMain by getting {
            dependsOn(jsWasmMain)
        }
    }
}

compose.experimental {
    web.application {}
}
