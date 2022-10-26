import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin(BuildPlugins.MULTIPLATFORM)
    id(BuildPlugins.COMPOSE_ID) version Lib.AndroidX.COMPOSE_VERSION
}

group = ProjectProperties.APPLICATION_ID
version = "1.0"

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(Lib.Grpc.NETTY)
                implementation(project(Lib.Project.common))
                implementation(compose.desktop.currentOs)
                api(Lib.Decompose.core)
                api(Lib.Decompose.coreJvm)
                api(Lib.Project.SLACK_DATA_JVM)
                api(Lib.Project.SLACK_DOMAIN_JVM)
            }
        }
        val jvmTest by getting{
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            dependencies{
                implementation(compose.uiTestJUnit4)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            modules("java.sql")
            packageName = "SlackClone"
            packageVersion = "1.0.0"
        }
    }
}
