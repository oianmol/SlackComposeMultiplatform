package dev.baseio.slackdata

actual fun readBinaryResource(resourceName: String): ByteArray {
    return ClassLoader
        .getSystemResourceAsStream(resourceName)!!
        .readBytes()
}