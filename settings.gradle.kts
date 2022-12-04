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

//include(":slack_domain_layer")
//include(":slack_data_layer")
//include(":encryptionlib")
include(":common")
include(":commoncomposeui")
//include(":slack_generate_protos")
include(":slack_protos")
