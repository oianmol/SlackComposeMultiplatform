package dev.baseio.slackdomain.usecases.users

import dev.baseio.slackdomain.LOGGED_IN_USER
import dev.baseio.slackdomain.datasources.local.SKLocalKeyValueSource
import dev.baseio.slackdomain.datasources.local.channels.SKLocalDataSourceReadChannels
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.users.DomainLayerUsers
import dev.baseio.slackdomain.usecases.channels.UseCaseSearchChannel
import dev.baseio.slackdomain.usecases.channels.UseCaseWorkspaceChannelRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class UseCaseFetchChannelsWithSearch(
    private val useCaseFetchLocalUsers: UseCaseFetchLocalUsers,
    private val useCaseSearchChannel: UseCaseSearchChannel,
    private val skLocalDataSourceReadChannels: SKLocalDataSourceReadChannels,
    private val skLocalKeyValueSource: SKLocalKeyValueSource,
) {
    operator fun invoke(workspaceId: String, search: String): Flow<List<DomainLayerChannels.SKChannel>> {
        val localUsers = useCaseFetchLocalUsers(workspaceId, search).map { skUsers ->
            skUsers.map { skUser ->
                val user = Json.decodeFromString<DomainLayerUsers.SKUser>(skLocalKeyValueSource.get(LOGGED_IN_USER.plus(workspaceId))!!)
                val dmChannel =
                    skLocalDataSourceReadChannels.getChannelByReceiverIdAndSenderId(workspaceId, skUser.uuid, user.uuid)
                val uuid = dmChannel?.uuid ?: (workspaceId + "${Clock.System.now().toEpochMilliseconds()}")
                DomainLayerChannels.SKChannel.SkDMChannel(
                    workId = workspaceId,
                    senderId = dmChannel?.senderId ?: user.uuid,
                    receiverId = skUser.uuid,
                    uuid = uuid,
                    deleted = false,
                    channelPublicKey = dmChannel?.channelPublicKey ?: DomainLayerUsers.SKSlackKey(ByteArray(0))
                ).apply {
                    channelName = skUser.name
                    pictureUrl = skUser.avatarUrl
                }
            }
        }

        val localChannels = useCaseSearchChannel(
            UseCaseWorkspaceChannelRequest(workspaceId = workspaceId, search)
        )

        return combine(localUsers, localChannels) { first, second ->
            return@combine first + second.filterIsInstance<DomainLayerChannels.SKChannel.SkGroupChannel>()
        }
    }
}
