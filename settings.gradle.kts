rootProject.name = "SlackCMP"
include(":composeApp")
include(":slack_protos")
include(":slack_generate_protos")
include(":capillary-kmp")
include(":server")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
