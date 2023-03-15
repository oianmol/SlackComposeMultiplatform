package dev.baseio.slackdomain.usecases.channels

data class UseCaseWorkspaceChannelRequest(
  val workspaceId: String,
  val channelId: String? = null,
  val limit: Int = 20,
  val offset: Int = 0
)