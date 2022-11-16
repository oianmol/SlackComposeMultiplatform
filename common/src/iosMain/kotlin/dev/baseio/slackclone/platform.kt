package dev.baseio.slackclone

actual fun platformType(): Platform = Platform.IOS

actual suspend fun fcmToken() = ""