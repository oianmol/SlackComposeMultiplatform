package dev.baseio.slackdomain.model.channel


interface DomainLayerChannels {
  data class SKChannel(
    val uuid: String? = null,
    val workspaceId:String,
    val name: String? = null,
    val createdDate: Long? = null,
    val modifiedDate: Long? = null,
    val isMuted: Boolean? = null,
    val isPrivate: Boolean? = null,
    val isStarred: Boolean? = false,
    val isShareOutSide: Boolean? = false,
    val isOneToOne: Boolean?,
    val avatarUrl: String?
  )
}
