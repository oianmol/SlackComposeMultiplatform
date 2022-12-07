import Lib.Networking

/** This file contains versions of all the dependencies used in the module  */

object BuildPlugins {
    private const val TOOLS_BUILD = "7.2.2"
    private const val KT_LINT = "11.0.0"
    private const val SAFE_ARGS = "2.3.5"
    private const val GOOGLE_SERVICES_VERSION = "4.3.13"

    const val ANDROID_TOOLS_BUILD_GRADLE = "com.android.tools.build:gradle:$TOOLS_BUILD"
    const val KTLINT_GRADLE_PLUGIN = "org.jlleitschuh.gradle:ktlint-gradle:$KT_LINT"
    const val SQLDELIGHT = "com.squareup.sqldelight:gradle-plugin:1.5.3"
    const val KMP_NATIVE_COROUTINES = "com.rickclephas.kmp:kmp-nativecoroutines-gradle-plugin:0.13.1"
    const val KOTLIN_GRADLE_PLUGIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Lib.Kotlin.KOTLIN_VERSION}"
    const val ANDROID_APPLICATION_PLUGIN = "com.android.application"
    const val ANDROID_LIBRARY_PLUGIN = "com.android.library"
    const val KOTLIN_ANDROID_PLUGIN = "kotlin-android"
    const val KOTLIN_PARCELABLE_PLUGIN = "kotlin-parcelize"
    const val KOTLIN_KAPT = "kotlin-kapt"
    const val KTLINT = "org.jlleitschuh.gradle.ktlint"
    const val SQLDELIGHT_ID: String = "com.squareup.sqldelight"
    const val COMPOSE_ID: String = "org.jetbrains.compose"
    const val SERIALIZATION = "plugin.serialization"
    const val MULTIPLATFORM = "multiplatform"
    const val SAFE_ARGS_KOTLIN = "androidx.navigation.safeargs.kotlin"
    const val GOOGLE_SERVICES = "com.google.gms:google-services:$GOOGLE_SERVICES_VERSION"
}


object Versions {
    const val koin = "3.1.4"
}

object Deps {

    object Kotlinx {
        const val datetime = "org.jetbrains.kotlinx:kotlinx-datetime:0.4.0"

        object JVM {
            const val coroutinesSwing = "org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.4"
        }

        object IOS {
            const val coroutinesX64 = "org.jetbrains.kotlinx:kotlinx-coroutines-core-iosx64:1.6.4"
            const val coroutinesArm64 = "org.jetbrains.kotlinx:kotlinx-coroutines-core-iosarm64:1.6.4"
            const val coroutinesiossimulatorarm64 = "org.jetbrains.kotlinx:kotlinx-coroutines-core-iossimulatorarm64:1.6.4"
        }

        object Android {
            const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4"
        }
    }

    object SqlDelight {
        const val androidDriver = "com.squareup.sqldelight:android-driver:1.5.3"
        const val jvmDriver = "com.squareup.sqldelight:sqlite-driver:1.5.3"
        const val nativeDriver = "com.squareup.sqldelight:native-driver:1.5.3"
        const val runtime = "com.squareup.sqldelight:runtime:1.5.3"
    }

    object Koin {
        const val core = "io.insert-koin:koin-core:${Versions.koin}"
        const val core_jvm = "io.insert-koin:koin-core-jvm:${Versions.koin}"
        const val test = "io.insert-koin:koin-test:${Versions.koin}"
        const val android = "io.insert-koin:koin-android:${Versions.koin}"
    }

    object AndroidX {
        const val lifecycleViewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1"
    }
}

object Lib {
    object Project {
        const val SLACK_DOMAIN_COMMON = ":slack_domain_layer"
        const val SLACK_DATA_COMMON = ":slack_data_layer"
        const val CAPILLARY_KMP = ":encryptionlib"


        const val common = ":common"
        const val commonComposeUI = ":commoncomposeui"
    }

    object Decompose {
        const val VERSION = "1.0.0-beta-01"
        const val core = "com.arkivanov.decompose:decompose:$VERSION"
        const val coreJvm = "com.arkivanov.decompose:decompose-jvm:$VERSION"
        const val composejb = "com.arkivanov.decompose:extensions-compose-jetbrains:$VERSION-native-compose"
    }

    object Kotlin {
        const val KOTLIN_VERSION = "1.7.20"
        private const val KTX_CORE_VERSION = "1.7.0"
        const val KT_STD = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$KOTLIN_VERSION"
        const val KTX_CORE = "androidx.core:core-ktx:$KTX_CORE_VERSION"
    }

    object AndroidX {
        val securityCrypto = "androidx.security:security-crypto-ktx:1.1.0-alpha03"
        const val COMPOSE_VERSION = "1.2.1"
        const val COMPOSE_COMPILER_VERSION = COMPOSE_VERSION
        private const val COMPOSE_ACTIVITY_VERSION = "1.6.0"
        private const val COMPOSE_CONSTRAINT_LAYOUT_VERSION = "1.0.0"
        private const val COMPOSE_NAVIGATION_VERSION = "2.5.0-alpha01"
        private const val COMPOSE_COIL_VERSION = "2.2.0"
        private const val ACCOMPANIST_VERSION = "0.28.0"
        private const val ACCOMPANIST_FLOW_LAYOUT_VERSION = "0.24.13-rc"
        private const val WINDOW_METRICES_VERSION = "1.0.0"
        private const val ACTIVITY_VERSION = "1.6.0"
        private const val ACCOMPANIST_ADAPTIVE_VERSION = "0.26.4-beta"

        const val APP_COMPAT = "androidx.appcompat:appcompat:1.4.1"
        const val SPLASH_SCREEN = "androidx.core:core-splashscreen:1.0.0"

        // Compose
        const val ACTIVITY_COMPOSE = "androidx.activity:activity-compose:$COMPOSE_ACTIVITY_VERSION"
        const val CONSTRAINT_LAYOUT_COMPOSE =
            "androidx.constraintlayout:constraintlayout-compose:$COMPOSE_CONSTRAINT_LAYOUT_VERSION"
        const val COMPOSE_TOOLING = "androidx.compose.ui:ui-tooling:$COMPOSE_VERSION"
        const val COMPOSE_RUNTIME_SAVEABLE_uikitarm64 ="org.jetbrains.compose.runtime:runtime-saveable-uikitarm64:$COMPOSE_VERSION"
        const val COMPOSE_RUNTIME_SAVEABLE_uikitsimarm64 ="org.jetbrains.compose.runtime:runtime-saveable-uikitsimarm64:$COMPOSE_VERSION"
        const val COMPOSE_RUNTIME_SAVEABLE_uikitx64 ="org.jetbrains.compose.runtime:runtime-saveable-uikitx64:$COMPOSE_VERSION"


        const val COMPOSE_TOOLING_PREVIEW = "androidx.compose.ui:ui-tooling-preview:$COMPOSE_VERSION"
        const val COMPOSE_UI_UTIL = "androidx.compose.ui:ui-util:$COMPOSE_VERSION"
        const val ACCOMPANIST_SYSTEM_UI_CONTROLLER =
            "com.google.accompanist:accompanist-systemuicontroller:$ACCOMPANIST_VERSION"
        const val ACCOMPANIST_FLOW_LAYOUTS = "com.google.accompanist:accompanist-flowlayout:$ACCOMPANIST_VERSION"
        const val COIL_COMPOSE = "io.coil-kt:coil-compose:$COMPOSE_COIL_VERSION"
        const val COMPOSE_LIVEDATA = "androidx.compose.runtime:runtime-livedata:$COMPOSE_VERSION"
        const val COMPOSE_NAVIGATION = "androidx.navigation:navigation-compose:$COMPOSE_NAVIGATION_VERSION"
        const val MATERIAL_DESIGN = "androidx.compose.material:material:$COMPOSE_VERSION"
        const val ACCOMPANIST_INSETS = "com.google.accompanist:accompanist-insets:$ACCOMPANIST_VERSION"
        const val ACCOMPANIST_INSETS_UI = "com.google.accompanist:accompanist-insets-ui:$ACCOMPANIST_VERSION"
        const val ACCOMPANIST_COIL = "com.google.accompanist:accompanist-coil:0.14.0"
        const val ACCOMPANIST_PERMISSION = "com.google.accompanist:accompanist-permissions:$ACCOMPANIST_VERSION"
        const val ACCOMPANIST_ADAPTIVE = "com.google.accompanist:accompanist-adaptive:$ACCOMPANIST_ADAPTIVE_VERSION"
        const val COMPOSE_JUNIT = "androidx.compose.ui:ui-test-junit4:$COMPOSE_VERSION"
        const val COMPOSE_TEST_MANIFEST = "androidx.compose.ui:ui-test-manifest:$COMPOSE_VERSION"
        const val COMPOSE_WINDOW_MATRICES = "androidx.window:window:$WINDOW_METRICES_VERSION"
        const val ACTIVITY_KTX = "androidx.activity:activity-ktx:$ACTIVITY_VERSION"

        const val PROFILE_INSTALLER = "androidx.profileinstaller:profileinstaller:1.2.0"

        // Constraint layout
        const val CONSTRAINT_LAYOUT = "androidx.constraintlayout:constraintlayout-compose:1.0.1"
    }

    object Room {
        private const val roomVersion = "2.4.1"
        const val roomRuntime = "androidx.room:room-runtime:$roomVersion"
        const val roomCompiler = "androidx.room:room-compiler:$roomVersion"

        // optional - Kotlin Extensions and Coroutines support for Room
        const val roomKtx = "androidx.room:room-ktx:$roomVersion"

        // optional - Paging 3 Integration
        const val roomPaging = "androidx.room:room-paging:2.4.1"
    }

    object Async {
        private const val COROUTINES_VERSION = "1.6.4"

        const val COROUTINES = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$COROUTINES_VERSION"
        const val COROUTINES_TEST = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$COROUTINES_VERSION"

        const val COROUTINES_ANDROID =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:$COROUTINES_VERSION"
    }

    object Networking {
        private const val RETROFIT_VERSION = "2.9.0"
        const val OKHTTP_VERSION = "4.7.2"
        const val ktor_version = "2.1.0"
        const val ktor_core = ("io.ktor:ktor-client-core:$ktor_version")
        const val ktor_cio = ("io.ktor:ktor-client-cio:$ktor_version")
        const val KTOR_JVM = "io.ktor:ktor-client-java:$ktor_version"
        val KTOR_DARWIN = "io.ktor:ktor-client-darwin:$ktor_version"
        const val RETROFIT = "com.squareup.retrofit2:retrofit:$RETROFIT_VERSION"
        const val RETROFIT_GSON = "com.squareup.retrofit2:converter-gson:$RETROFIT_VERSION"
        const val LOGGING = "com.squareup.okhttp3:logging-interceptor:$OKHTTP_VERSION"
    }

    object Serialization {
        private const val GSON_VERSION = "2.8.8"
        const val GSON = "com.google.code.gson:gson:$GSON_VERSION"
    }

    object Logger {
        private const val TIMBER_VERSION = "5.0.1"
        const val TIMBER = "com.jakewharton.timber:timber:$TIMBER_VERSION"
    }

    object Grpc {
        const val NETTY = "io.grpc:grpc-netty-shaded:1.49.2"
        const val OKHTTP = "io.grpc:grpc-okhttp:1.50.0"
    }

    object Persistence {
        const val SQLDELIGHT_NATIVE = "com.squareup.sqldelight:native-driver:1.5.3"
    }

    object Multiplatform {

        const val kamelImage = "com.alialbaali.kamel:kamel-image:0.4.0"
        const val mokoPaging = "dev.icerock.moko:paging:0.7.2"
    }

    object Firebase {
        private const val BOM_VERSION = "31.0.2"
        const val BOM = "com.google.firebase:firebase-bom:$BOM_VERSION"
        const val CLOUD_MESSAGING = "com.google.firebase:firebase-messaging-ktx"
    }
}

object TestLib {
    private const val COROUTINES_VERSION = "1.6.4"
    private const val ANDROID_JUNIT_VERSION = "1.1.3"
    private const val ROBO_ELECTRIC_VERSION = "4.3"
    private const val ARCH_CORE_VERSION = "2.1.0"
    private const val CORE_TEST_VERSION = "1.2.0"
    private const val JUNIT_VERSION = "4.13.2"
    private const val nav_version = "2.3.5"

    const val COROUTINES = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$COROUTINES_VERSION"
    const val ROBO_ELECTRIC = "org.robolectric:robolectric:$ROBO_ELECTRIC_VERSION"
    const val MOCK_WEB_SERVER = "com.squareup.okhttp3:mockwebserver:${Networking.OKHTTP_VERSION}"
    const val CORE_TEST = "androidx.test:core-ktx:$CORE_TEST_VERSION"
    const val JUNIT = "junit:junit:$JUNIT_VERSION"
    const val RUNNER = "androidx.test:runner:1.4.0"
    const val ANDROID_JUNIT = "androidx.test.ext:junit-ktx:$ANDROID_JUNIT_VERSION"
    const val ARCH_CORE = "androidx.arch.core:core-testing:$ARCH_CORE_VERSION"
    const val MOCKK = "io.mockk:mockk:1.10.5"
}
