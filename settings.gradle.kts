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

include(":generate-proto")
include(":protos")

project(":generate-proto").projectDir = file("../slackdata/generate-proto")
project(":protos").projectDir = file("../slackdata/protos")

include(":android")
include(":desktop")
include(":common")
include(":iOSApp")
