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
include(":iOSApp")
include(":slack_data_layer")
include(":slack_domain_layer")
include(":slack_generate_protos")
include(":slack_protos")

include(":grpc-multiplatform-lib")
project(":grpc-multiplatform-lib").projectDir = file("/Users/anmolverma/IdeaProjects/GRPC-Kotlin-Multiplatform/grpc-multiplatform-lib")


