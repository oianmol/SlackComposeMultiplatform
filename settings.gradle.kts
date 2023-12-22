pluginManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        mavenCentral()
        mavenLocal()
        maven {
            setUrl("https://repo1.maven.org/maven2/")
        }
        maven { setUrl("https://jitpack.io") }
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "SlackJetpackCompose"

include(":androidApp")
include(":desktop")
include(":webApp")

include(":commoncomposeui")
include(":common")

include(":slack_domain_layer")
include(":slack_data_layer")

include(":slack_generate_protos")
include(":slack_protos")

include(":capillary-kmp")
include(":capillaryios")

include(":slack_multiplatform_grpc_server")