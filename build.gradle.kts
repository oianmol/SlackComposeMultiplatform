buildscript {
    repositories {
        gradlePluginPortal()
        jcenter()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.0")
        classpath("com.android.tools.build:gradle:7.2.2")
        classpath("com.squareup.sqldelight:gradle-plugin:1.5.3")
    }
}

group = "dev.baseio.slackclone"
version = "1.0"

object GithubRepo {
    val name: String? = System.getenv("GITHUB_REPOSITORY")
    val path: String = "https://www.github.com/$name"
    val packages: String = "https://maven.pkg.github.com/$name"
    val ref: String? = System.getenv("GITHUB_REF")
}

allprojects {
    repositories {
        repositories.maven {
            name = "github"
            url = project.uri(GithubRepo.packages)
            credentials(PasswordCredentials::class)
        }.takeIf { GithubRepo.name != null }
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}