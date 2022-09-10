buildscript {
    repositories {
        gradlePluginPortal()
        jcenter()
        google()
        mavenCentral()
        mavenLocal()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.0")
        classpath("com.android.tools.build:gradle:7.2.2")
        classpath("com.squareup.sqldelight:gradle-plugin:1.5.3")
    }
}

group = "dev.baseio.slackclone"
version = "1.0"

allprojects {
    repositories {
        maven {
            name = "slackdata"
            url = uri("https://maven.pkg.github.com/anmol92verma/slack-data")
            credentials(PasswordCredentials)
        }
        google()
        mavenLocal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}