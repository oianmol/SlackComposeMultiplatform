package dev.baseio.slackdomain.model.users

import kotlinx.serialization.Serializable

interface DomainLayerUsers {
  @Serializable
  data class SKUser(
    val uuid: String,
    val workspaceId:String,
    val gender: String?,
    val name: String,
    val location: String?,
    val email: String,
    val username: String,
    val userSince: Long,
    val phone: String,
    val avatarUrl: String
  )
}
