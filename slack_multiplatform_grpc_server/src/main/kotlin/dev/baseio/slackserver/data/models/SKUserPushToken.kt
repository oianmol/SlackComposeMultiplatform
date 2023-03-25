package dev.baseio.slackserver.data.models

data class SKUserPushToken(val uuid: String, val userId: String, val platform: Int, val token:String)
