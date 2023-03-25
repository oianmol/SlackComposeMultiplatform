package dev.baseio.slackserver.data.models

data class SkWorkspace(
    val uuid: String,
    val name: String,
    val domain: String,
    val picUrl: String?,
    val modifiedTime: Long
)