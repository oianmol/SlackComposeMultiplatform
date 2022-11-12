buildscript {
    repositories {
        gradlePluginPortal()
        jcenter()
        google()
        mavenLocal()
        mavenCentral()
    }
    dependencies {
        classpath(BuildPlugins.ANDROID_TOOLS_BUILD_GRADLE)
        classpath(BuildPlugins.KOTLIN_GRADLE_PLUGIN)
        classpath(BuildPlugins.SQLDELIGHT)
        classpath(BuildPlugins.KTLINT_GRADLE_PLUGIN)
    }
}

group = "dev.baseio.slackclone"
version = "1.0"

/*subprojects {
    if(this.name!="capillary_kmp"){
        apply(plugin = BuildPlugins.KTLINT)
    }
}*/

apply(from = teamPropsFile("git-hooks.gradle.kts"))

fun teamPropsFile(propsFile: String): File {
    val teamPropsDir = file("team-props")
    return File(teamPropsDir, propsFile)
}

allprojects {
    repositories {
        google()
        mavenLocal()
        mavenCentral()
    }
}
