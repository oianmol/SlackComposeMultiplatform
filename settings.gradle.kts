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
include(":android")
include(":desktop")
include(":common")
include(":iOSApp")
