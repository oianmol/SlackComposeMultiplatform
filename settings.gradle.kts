pluginManagement {
    repositories {
        google()
        jcenter()
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
        maven {
            setUrl("https://repo1.maven.org/maven2/")
        }
    }
}
rootProject.name = "SlackJetpackCompose"

include(":android")
include(":desktop")
include(":common")
//include(":iOSApp")
include(":slack_data_layer")
include(":slack_domain_layer")

include(":slack_generate_protos")
include(":slack_protos")

include(":capillary_kmp")

include(":generate_protos")
project(":generate_protos").projectDir = file("capillary_kmp/generate_protos")

include(":protos")
project(":protos").projectDir = file("capillary_kmp/protos")
