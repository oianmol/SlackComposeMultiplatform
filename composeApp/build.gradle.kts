import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.net.InetAddress
import java.util.Enumeration
import java.net.NetworkInterface
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetWithSimulatorTests
import io.github.timortel.kotlin_multiplatform_grpc_plugin.GrpcMultiplatformExtension.OutputTarget

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.buildConfig)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.sqlDelight)
    alias(libs.plugins.kotlin.native.cocoapods)
    alias(libs.plugins.grpcKmp)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    jvm()

    js {
        useCommonJs()

        browser {
            webpackTask {
                output.libraryTarget = "commonjs2"
            }
        }

        binaries.executable()
    }
    
    cocoapods {
        summary = "SlackIos"
        homepage = "https://github.com/oianmol"
        ios.deploymentTarget = "14.1"
        version = "1.0.0"
        pod("gRPC-ProtoRPC", moduleName = "GRPCClient")
        pod("Protobuf")
    }
    
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
    
    applyDefaultHierarchyTemplate()

    listOf(
        iosX64(),
        iosArm64(),
       // iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }

        val platform = if (it.targetName == "iosArm64") "iphoneos" else "iphonesimulator"

        if (it is KotlinNativeTargetWithSimulatorTests) {
            it.testRuns.forEach { tr ->
                tr.deviceId = properties["iosSimulatorName"] as? String ?: "iPhone 14"
            }
        }

        it.binaries.all {
            linkerOpts("-ObjC")
            // linkerOpts(opts)
            linkerOpts("-L/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/lib/swift/$platform")
            linkerOpts("-L/usr/lib/swift/")
        }

        it.binaries.getTest(org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType.DEBUG).apply {
            linkerOpts(
                "-rpath",
                "/usr/lib/swift"
            )
        }
    }

    sourceSets {
        all {
            languageSettings {
                optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
            }
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.foundation)
            implementation(compose.materialIconsExtended)
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)
            implementation(libs.decompose.core)
            implementation(libs.decompose.compose)
            implementation(libs.composeImageLoader)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.multiplatformSettings)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.sqlDelight.driver.extensions)
            implementation(libs.kamel.image)
            api(libs.grpc.multiplatform.lib)
            implementation(project(":capillary-kmp"))
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        androidMain.dependencies {
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.activityCompose)
            implementation(libs.compose.uitooling)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.sqlDelight.driver.android)
            implementation(libs.koin.android)
            implementation(libs.securitycrypto)
            implementation(libs.grpc.okhttp)

            implementation(libs.splash.screen)
            implementation(libs.accompanist.permission)
            implementation(libs.firebase.messaging.ktx)

            // CameraX
            api(libs.androidx.camera.camera2)
            api(libs.androidx.camera.lifecycle)
            api(libs.androidx.camera.view)
            api(libs.androidx.camera.video)
            api(libs.androidx.camera.extensions)
            implementation(libs.guava)
            // Zxing
            api(libs.zxing.core)
            api(libs.firebase.core)
            api(libs.firebase.messaging)
            implementation(libs.barcode.scanning)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.common)
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.sqlDelight.driver.sqlite)
            implementation(libs.windows.registry.util)
            api(libs.zxing.javase)
        }

        jsMain.dependencies {
            implementation(compose.html.core)
            implementation(libs.sqlDelight.driver.js)
            api(npm("google-protobuf", "^3.19.1"))
            api(npm("grpc-web", "^1.3.0"))
            api(npm("protobufjs", "^6.11.2"))
            api(npm("node-forge","^1.3.1"))
            api(npm("qrcode", "^1.5.3"))
            implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.0.1"))
            implementation(npm("sql.js", "1.8.0"))
            implementation(devNpm("copy-webpack-plugin", "9.1.0"))
        }

        iosMain.dependencies {
            implementation(libs.sqlDelight.driver.native)
        }

    }
}

android {
    namespace = "dev.oianmol.slack"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        targetSdk = 34

        applicationId = "dev.oianmol.slack"
        versionCode = 1
        versionName = "1.0.0"
    }
    sourceSets["main"].apply {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        res.srcDirs("src/androidMain/resources")
        resources.srcDirs("src/commonMain/resources")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
}

sqldelight {
    databases {
        create("SlackDB") {
            packageName.set("dev.baseio.database")
            generateAsync.set(true)
        }
        linkSqlite.set(true)
    }
   
}

grpcKotlinMultiplatform {
    with(kotlin) {
        targetSourcesMap.put(OutputTarget.COMMON, listOf(kotlin.sourceSets.commonMain.get()))
        targetSourcesMap.put(OutputTarget.IOS, listOf(kotlin.sourceSets.iosMain.get()))
        targetSourcesMap.put(OutputTarget.JVM, listOf(kotlin.sourceSets.androidMain.get()))
        targetSourcesMap.put(OutputTarget.JS, listOf(kotlin.sourceSets.jsMain.get()))
    }

    // Specify the folders where your proto files are located, you can list multiple.
    protoSourceFolders.set(listOf(projectDir.parentFile.resolve("slack_protos/src/main/proto").also {
        println(it)
    }))
}


compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "dev.oianmol.slack.desktopApp"
            packageVersion = "1.0.0"
        }
    }
}

compose.experimental {
    web.application {}
}


buildConfig {
    packageName.set("dev.baseio.slackclone")

    buildConfigField("ipAddr", """${ipv4()}""")
    
}

fun ipv4(): String {
    var result: InetAddress? = null
    val interfaces: Enumeration<java.net.NetworkInterface> = (NetworkInterface.getNetworkInterfaces())
    while (interfaces.hasMoreElements()) {
        val addresses: Enumeration<InetAddress> = interfaces.nextElement().getInetAddresses()
        while (addresses.hasMoreElements()) {
            val address = addresses.nextElement()
            if (!address.isLoopbackAddress) {
                if (address.isSiteLocalAddress) {
                    return address.hostAddress
                } else if (result == null) {
                    result = address
                }
            }
        }
    }
    return (result ?: InetAddress.getLocalHost()).hostAddress
}

afterEvaluate {
    rootProject.extensions.configure<org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension> {
        versions.webpackCli.version = "4.10.0"
    }
}