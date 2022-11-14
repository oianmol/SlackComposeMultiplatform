package dev.baseio.slackclone

enum class Platform {
  ANDROID,IOS,JVM
}

expect fun platformType() :Platform