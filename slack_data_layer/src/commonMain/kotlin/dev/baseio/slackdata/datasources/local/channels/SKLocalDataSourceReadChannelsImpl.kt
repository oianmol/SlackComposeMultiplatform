package dev.baseio.slackdata.datasources.local.channels

import database.SkDMChannel
import database.SkPublicChannel
import dev.baseio.database.SlackDB
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdata.local.asFlow
import dev.baseio.slackdata.local.mapToList
import dev.baseio.slackdata.mapper.EntityMapper
import dev.baseio.slackdomain.LOGGED_IN_USER
import dev.baseio.slackdomain.datasources.local.SKLocalKeyValueSource
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceReadChannels
import dev.baseio.slackdomain.datasources.local.users.SKLocalDataSourceUsers
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import dev.baseio.slackdomain.usecases.channels.UseCaseWorkspaceChannelRequest
import dev.baseio.slackdomain.usecases.channels.otherUserInDMChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class SKLocalDataSourceReadChannelsImpl(
    private val slackChannelDao: SlackDB,
    private val publicChannelMapper: EntityMapper<DomainLayerChannels.SKChannel, SkPublicChannel>,
    private val directChannelMapper: EntityMapper<DomainLayerChannels.SKChannel, SkDMChannel>,
    private val skKeyValueData: SKLocalKeyValueSource,
    private val coroutineMainDispatcherProvider: CoroutineDispatcherProvider,
    private val skLocalDataSourceUsers: SKLocalDataSourceUsers
) : SKLocalDataSourceReadChannels {

    override fun fetchChannelsOrByName(
        workspaceId: String,
        params: String?
    ): Flow<List<DomainLayerChannels.SKChannel>> {
        val flow = publicChannels(params, workspaceId)

        val flowDMChannels = allDmChannelsFlow(workspaceId)
        return combine(flow, flowDMChannels) { a, b -> a + b }
    }

    private fun publicChannels(
        params: String?,
        workspaceId: String
    ) = kotlin.run {
        params?.takeIf { it.isNotEmpty() }?.let {
            slackChannelDao.slackDBQueries.selectAllPublicChannelsByName(workspaceId, params)
                .asFlow()
                .mapToList(coroutineMainDispatcherProvider.default)

        } ?: run {
            slackChannelDao.slackDBQueries.selectAllPublicChannels(workspaceId).asFlow()
                .mapToList(coroutineMainDispatcherProvider.default)
        }
    }.map { skPublicChannels ->
        skPublicChannels.map { skPublicChannel ->
            publicChannelMapper.mapToDomain(skPublicChannel)
        }
    }

    override suspend fun getChannelByReceiverId(
        workspaceId: String,
        uuid: String
    ): DomainLayerChannels.SKChannel.SkDMChannel? {
        return withContext(coroutineMainDispatcherProvider.io) {
            val channel =
                slackChannelDao.slackDBQueries.selectDMChannelByReceiverId(workspaceId, uuid).executeAsOneOrNull()
            channel?.let { directChannelMapper.mapToDomain(it) } as DomainLayerChannels.SKChannel.SkDMChannel?
        }
    }

    override suspend fun getChannelByChannelId(channelId: String): DomainLayerChannels.SKChannel? {
        return withContext(coroutineMainDispatcherProvider.io) {
            val channel =
                slackChannelDao.slackDBQueries.selectDMChannelByChannelId(channelId).executeAsOneOrNull()
            val skDmChannel = channel?.let { directChannelMapper.mapToDomain(it) }
            val publicChannel =
                slackChannelDao.slackDBQueries.selectPublicChannelByChannelId(channelId).executeAsOneOrNull()
            val dkPublicChannel =
                publicChannel?.let { publicChannelMapper.mapToDomain(it) } as DomainLayerChannels.SKChannel
            skDmChannel ?: dkPublicChannel
        }
    }

    override suspend fun getChannelByReceiverIdAndSenderId(
        workspaceId: String,
        receiverId: String,
        senderId: String
    ): DomainLayerChannels.SKChannel.SkDMChannel? {
        return withContext(coroutineMainDispatcherProvider.io) {
            val channel =
                slackChannelDao.slackDBQueries.selectDMChannelByReceiverIdAndSenderId(workspaceId, receiverId, senderId)
                    .executeAsOneOrNull()
            channel?.let { directChannelMapper.mapToDomain(it) } as DomainLayerChannels.SKChannel.SkDMChannel?
        }
    }

    override suspend fun getChannelById(workspaceId: String, uuid: String): DomainLayerChannels.SKChannel? {
        return withContext(coroutineMainDispatcherProvider.io) {
            kotlin.run {
                slackChannelDao.slackDBQueries.selectPublicChannelById(workspaceId, uuid).executeAsOneOrNull()?.let {
                    publicChannelMapper.mapToDomain(it)
                }
                    ?: slackChannelDao.slackDBQueries.selectDMChannelById(workspaceId, uuid).executeAsOneOrNull()?.let {
                        directChannelMapper.mapToDomain(it)
                    }
            }.run {
                if (this is DomainLayerChannels.SKChannel.SkDMChannel) {
                    this.populateDMChannelWithOtherUser(skKeyValueData, skLocalDataSourceUsers,this.workspaceId)
                }
                this
            }
        }
    }

    override suspend fun channelCount(workspaceId: String): Long {
        return slackChannelDao.slackDBQueries.countPublicChannels(workspaceId).executeAsOne()
    }

    override fun fetchAllChannels(workspaceId: String): Flow<List<DomainLayerChannels.SKChannel>> {
        val publicFlow: Flow<List<DomainLayerChannels.SKChannel>> = allPublicChannelsFlow(workspaceId)
        val dmFlow: Flow<List<DomainLayerChannels.SKChannel>> = allDmChannelsFlow(workspaceId)

        return combine(publicFlow, dmFlow) { a, b ->
            a + b
        }
    }

    private fun allPublicChannelsFlow(workspaceId: String) =
        slackChannelDao.slackDBQueries.selectAllPublicChannels(workspaceId).asFlow()
            .mapToList(coroutineMainDispatcherProvider.default)
            .map { skPublicChannels ->
                skPublicChannels.map { skPublicChannel ->
                    publicChannelMapper.mapToDomain(skPublicChannel)
                }
            }

    private fun allDmChannelsFlow(workspaceId: String) =
        slackChannelDao.slackDBQueries.selectAllDMChannels(workspaceId).asFlow()
            .mapToList(coroutineMainDispatcherProvider.default).map {
                it.map { skDMChannel ->
                    directChannelMapper.mapToDomain(skDMChannel)
                }
            }.map { skChannelList ->
                skChannelList.map { skChannel ->
                    if (skChannel is DomainLayerChannels.SKChannel.SkDMChannel) {
                        skChannel.populateDMChannelWithOtherUser(
                            skKeyValueData,
                            skLocalDataSourceUsers,
                            workspaceId
                        )
                    }
                    skChannel
                }
            }

    override suspend fun getChannel(request: UseCaseWorkspaceChannelRequest): DomainLayerChannels.SKChannel? {
        return getChannelById(request.workspaceId, request.channelId!!)
    }


}

fun DomainLayerChannels.SKChannel.SkDMChannel.populateDMChannelWithOtherUser(
    skKeyValueData: SKLocalKeyValueSource,
    skLocalDataSourceUsers: SKLocalDataSourceUsers,
    workspaceId: String
) {
    val loggedInUser = skKeyValueData.loggedInUser(workspaceId)
    val otherUserId = loggedInUser.otherUserInDMChannel(this)
    val user = skLocalDataSourceUsers.getUser(this.workspaceId, otherUserId)
    user?.let { skUser ->
        this.channelName = skUser.name
        this.pictureUrl = skUser.avatarUrl
    }
}

fun SKLocalKeyValueSource.loggedInUser(workspaceId: String): DomainLayerUsers.SKUser {
    return Json.decodeFromString(this.get(LOGGED_IN_USER.plus(workspaceId))!!)
}