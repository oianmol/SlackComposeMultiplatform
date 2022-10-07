version = "unspecified"
plugins {
    `java-library`
}

java {
    sourceSets.getByName("main").resources.srcDir("src/main/proto")
}