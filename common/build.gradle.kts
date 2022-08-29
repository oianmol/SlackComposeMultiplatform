import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.2.0-alpha01-dev770"
    id("com.android.library")
    id("com.squareup.sqldelight")
}

group = "dev.baseio.slackclone"
version = "1.0"

val ktor_version = "2.1.0"
val DECOMPOSE = "1.0.0-alpha-04"

object Versions {
    const val koin = "3.1.4"
}

object Deps {

    object Koin {
        const val core = "io.insert-koin:koin-core:${Versions.koin}"
        const val core_jvm = "io.insert-koin:koin-core-jvm:${Versions.koin}"
        const val test = "io.insert-koin:koin-test:${Versions.koin}"
        const val android = "io.insert-koin:koin-android:${Versions.koin}"
    }

}

kotlin {
    android{
    }
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("androidx.paging:paging-compose:1.0.0-alpha16")
                implementation("androidx.paging:paging-common-ktx:3.1.1")
                implementation("com.alialbaali.kamel:kamel-image:0.4.0")
                implementation("com.squareup.sqldelight:runtime:1.5.3")
                implementation(Deps.Koin.core)
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                implementation(compose.preview)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(Deps.Koin.test)
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Deps.Koin.android)
                implementation("com.squareup.sqldelight:android-driver:1.5.3")
                implementation("io.coil-kt:coil-compose:2.2.0")
                api("androidx.constraintlayout:constraintlayout-compose:1.0.1")
                implementation("io.ktor:ktor-client-android:$ktor_version")
                api("androidx.appcompat:appcompat:1.5.0")
                api("androidx.core:core-ktx:1.8.0")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation("com.squareup.sqldelight:sqlite-driver:1.5.3")
                implementation("io.ktor:ktor-client-java:$ktor_version")
                implementation("com.alialbaali.kamel:kamel-image:0.4.0")
                api(compose.preview)
                implementation(Deps.Koin.core_jvm)
            }
        }
        val desktopTest by getting
    }
}

sqldelight {
    database("SlackDB") {
        packageName = "dev.baseio.database"
    }
}


android {
    compileSdk = 31
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 24
        targetSdk = 31
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
