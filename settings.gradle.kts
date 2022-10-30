pluginManagement {
    repositories {
        google()
        jcenter()
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
rootProject.name = "SlackJetpackCompose"

include(":android")
include(":desktop")
include(":common")
include(":iOSApp")
include(":slack_data_layer")
include(":slack_domain_layer")
include(":slack_generate_protos")
include(":slack_protos")
