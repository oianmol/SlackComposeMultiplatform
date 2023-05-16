package dev.baseio.slackdomain.datasources.local.channels

import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import kotlinx.coroutines.flow.Flow

interface SKLocalDataSourceChannelMembers {
    suspend fun save(members: List<DomainLayerChannels.SkChannelMember>)
    fun get(workspaceId: String, channelId: String): Flow<List<DomainLayerChannels.SkChannelMember>>
    suspend fun getNow(workspaceId: String, channelId: String): List<DomainLayerChannels.SkChannelMember>
    fun getChannelPrivateKeyForMe(
        workspaceId: String,
        channelId: String,
        uuid: String
    ): List<DomainLayerChannels.SkChannelMember>
}
