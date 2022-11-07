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
include(":capillary_kmp")


include(":capillary_generate_protos")
project(":capillary_generate_protos").projectDir = file("capillary_kmp/capillary_generate_protos")

include(":capillary_protos")
project(":capillary_protos").projectDir = file("capillary_kmp/capillary_protos")
