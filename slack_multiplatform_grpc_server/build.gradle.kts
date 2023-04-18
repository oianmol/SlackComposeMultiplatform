plugins {
    kotlin("jvm")
    application
}

group = "dev.baseio.slackserver"
version = "1.0"

dependencies {
    implementation("javax.mail:mail:1.5.0-b01")
    testImplementation(kotlin("test"))
    testImplementation("app.cash.turbine:turbine:0.12.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    testImplementation("io.grpc:grpc-testing:1.51.0")

    implementation("org.bouncycastle:bcprov-jdk16:1.46")
    implementation("com.google.firebase:firebase-admin:9.1.1")
    implementation(project(":capillary-kmp"))

    implementation(project(":slack_generate_protos"))

    implementation("io.insert-koin:koin-core:3.2.2")

    implementation("io.grpc:grpc-netty-shaded:1.50.2")
    implementation("com.google.zxing:core:3.5.0")
    implementation("com.google.zxing:javase:3.5.0")

    // mongodb
    implementation("org.litote.kmongo:kmongo:4.7.2")
    implementation("org.litote.kmongo:kmongo-async:4.7.2")
    implementation("org.litote.kmongo:kmongo-coroutine:4.7.2")

    // jwt
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-orgjson:0.11.5")

    // passwords
    implementation("at.favre.lib:bcrypt:0.9.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}
