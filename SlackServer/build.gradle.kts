plugins {
  kotlin("jvm") version "1.7.10"
  application
  id("com.squareup.sqldelight") version "1.5.3"
}

sqldelight {
  database("SlackCloneDB") { // This will be the name of the generated database class.
    packageName = "dev.baseio"
    dialect = "mysql"
  }
}

group = "dev.baseio.slackserver"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  testImplementation(kotlin("test"))
  implementation("io.grpc:grpc-netty-shaded:1.47.0")
  implementation(project(":generate-proto"))
  val sqldelightVersion = "1.5.3"
  implementation("com.squareup.sqldelight:runtime-jvm:$sqldelightVersion")
  implementation("com.squareup.sqldelight:jdbc-driver:$sqldelightVersion")
  implementation("com.squareup.sqldelight:coroutines-extensions:$sqldelightVersion")

  // Hikari JDBC connection pool
  implementation("com.zaxxer:HikariCP:5.0.1")

  // MySQL drivers
  implementation("com.h2database:h2:2.1.214")
  implementation("org.mariadb.jdbc:mariadb-java-client:3.0.6")
  implementation("mysql:mysql-connector-java:8.0.30")
  implementation("com.google.cloud.sql:mysql-socket-factory-connector-j-8:1.7.0")
  kotlin.sourceSets.getByName("main")
    .kotlin
    .srcDir(projectDir
      .resolve("build/generated/source/proto/main/kotlin")
      .canonicalPath)
  //grpckt ?

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