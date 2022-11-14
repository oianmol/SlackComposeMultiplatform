package dev.baseio.slackclone

enum class Platform {
  ANDROID,IOS,JVM
}

expect fun platformType() :Platform
expect suspend fun fcmToken():String