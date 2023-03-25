package dev.baseio.slackserver.data.models

data class SkUser(
  val uuid: String,
  val workspaceId: String,
  val gender: String?,
  val name: String,
  val location: String?,
  val email: String,
  val username: String,
  val userSince: Long,
  val phone: String,
  val avatarUrl: String,
  val publicKey: SKUserPublicKey
) {
  companion object {
    const val NAME = "skUser"
  }
}