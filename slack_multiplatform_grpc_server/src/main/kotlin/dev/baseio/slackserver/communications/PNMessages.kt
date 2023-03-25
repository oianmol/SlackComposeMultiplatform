package dev.baseio.slackserver.communications

import dev.baseio.slackserver.data.models.SKUserPushToken
import dev.baseio.slackserver.data.models.SkMessage
import dev.baseio.slackserver.data.models.SkUser
import dev.baseio.slackserver.data.sources.ChannelMemberDataSource
import dev.baseio.slackserver.data.sources.UserPushTokenDataSource
import dev.baseio.slackserver.data.sources.UsersDataSource

class PNMessages(
    private val channelMemberDataSource: ChannelMemberDataSource,
    private val userPushTokenDataSource: UserPushTokenDataSource,
    private val usersDataSource: UsersDataSource
) : PNSender<SkMessage>() {

    override suspend fun getSender(senderUserId: String, request: SkMessage): SkUser {
        return usersDataSource.getUser(senderUserId, request.workspaceId)!!
    }

    override suspend fun getPushTokens(request: SkMessage): List<SKUserPushToken> {
        val tokens = mutableListOf<SKUserPushToken>()
        channelMemberDataSource.getMembers(request.workspaceId, request.channelId).map { it.memberId }
            .let { skChannelMembers ->
                tokens.addAll(userPushTokenDataSource.getPushTokensFor(skChannelMembers))
            }
        return tokens
    }

}