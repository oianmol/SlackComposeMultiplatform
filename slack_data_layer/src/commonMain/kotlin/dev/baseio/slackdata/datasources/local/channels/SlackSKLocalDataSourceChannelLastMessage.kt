package dev.baseio.slackdata.datasources.local.channels

import database.SelectLastMessageOfChannel
import database.SkDMChannel
import database.SkPublicChannel
import database.SlackMessage
import dev.baseio.database.SlackDB
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdata.local.asFlow
import dev.baseio.slackdata.local.mapToList
import dev.baseio.slackdata.mapper.EntityMapper
import dev.baseio.slackdomain.datasources.local.SKLocalKeyValueSource
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceChannelLastMessage
import dev.baseio.slackdomain.datasources.local.messages.IMessageDecrypter
import dev.baseio.slackdomain.datasources.local.users.SKLocalDataSourceUsers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SlackSKLocalDataSourceChannelLastMessage(
    private val slackChannelDao: SlackDB,
    private val skKeyValueData: SKLocalKeyValueSource,
    private val messagesMapper: EntityMapper<DomainLayerMessages.SKMessage, SlackMessage>,
    private val publicChannelMapper: EntityMapper<DomainLayerChannels.SKChannel, SkPublicChannel>,
    private val dmChannelMapper: EntityMapper<DomainLayerChannels.SKChannel, SkDMChannel>,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val skLocalDataSourceUsers: SKLocalDataSourceUsers,
    private val iMessageDecrypter: IMessageDecrypter
) : SKLocalDataSourceChannelLastMessage {
    override fun fetchChannelsWithLastMessage(workspaceId: String): Flow<List<DomainLayerMessages.SKLastMessage>> {
        val chatPager = slackChannelDao.slackDBQueries.selectLastMessageOfChannel(workspaceId)
            .asFlow()
            .mapToList(coroutineDispatcherProvider.default)
        return chatPager.map { selectLastMessageOfChannels ->
            selectLastMessageOfChannels.mapNotNull { channelsWithLastMessage ->
                // here we are fetching the channel details from the channelId of last message
                val message = slackMessage(channelsWithLastMessage)
                val channel = skPublicChannel(workspaceId, channelsWithLastMessage)

                channel?.let {
                    return@mapNotNull DomainLayerMessages.SKLastMessage(
                        publicChannelMapper.mapToDomain(channel),
                        messagesMapper.mapToDomain(message)
                    )
                }
                val dmChannel = skDMChannel(workspaceId, channelsWithLastMessage)
                dmChannel?.let { skDMChannel ->
                    val domainChannel = dmChannelMapper.mapToDomain(skDMChannel)
                    (domainChannel as DomainLayerChannels.SKChannel.SkDMChannel).populateDMChannelWithOtherUser(
                        skKeyValueData,
                        skLocalDataSourceUsers,
                        workspaceId
                    )
                    return@mapNotNull DomainLayerMessages.SKLastMessage(
                        domainChannel,
                        messagesMapper.mapToDomain(message)
                    )
                }
                return@mapNotNull null
            }
        }.map { skLastMessageList ->
            skLastMessageList.map { skLastMessage ->
                val decrypted = iMessageDecrypter.decrypted(skLastMessage.message)
                skLastMessage.copy(message = decrypted ?: skLastMessage.message)
            }
        }
    }

    private fun skDMChannel(
        workspaceId: String,
        channelsWithLastMessage: SelectLastMessageOfChannel
    ) = slackChannelDao.slackDBQueries.selectDMChannelById(workspaceId, channelsWithLastMessage.channelId)
        .executeAsOneOrNull()

    private fun skPublicChannel(
        workspaceId: String,
        channelsWithLastMessage: SelectLastMessageOfChannel
    ) = slackChannelDao.slackDBQueries.selectPublicChannelById(workspaceId, channelsWithLastMessage.channelId)
        .executeAsOneOrNull()

    private fun slackMessage(channelsWithLastMessage: SelectLastMessageOfChannel) = SlackMessage(
        channelsWithLastMessage.uuid,
        channelsWithLastMessage.workspaceId,
        channelsWithLastMessage.channelId,
        channelsWithLastMessage.messageFirst,
        channelsWithLastMessage.messageSecond,
        channelsWithLastMessage.sender,
        channelsWithLastMessage.createdDate,
        channelsWithLastMessage.modifiedDate,
        channelsWithLastMessage.isDeleted,
        channelsWithLastMessage.isSynced,
    )
}