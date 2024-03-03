plugins {
    kotlin("jvm")
    application
}

group = "dev.baseio.slackserver"
version = "1.0"

dependencies {
    implementation(libs.mail)
    testImplementation(kotlin("test"))
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.grpc.testing)

    implementation(libs.bcprov.jdk16)
    implementation(libs.firebase.admin)
    implementation(libs.thymeleaf)

    implementation(project(":slack_generate_protos"))

    implementation(libs.koin.core)

    implementation(libs.grpc.netty)
    implementation(libs.zxing.core)
    implementation(libs.zxing.javase)

    // mongodb
    implementation(libs.kmongo)
    implementation(libs.kmongo.async)
    implementation(libs.kmongo.coroutine)

    // jwt
    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.orgjson)

    // passwords
    implementation(libs.bcrypt)
    implementation(project(":capillary-kmp"))

}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("MainKt")
}
