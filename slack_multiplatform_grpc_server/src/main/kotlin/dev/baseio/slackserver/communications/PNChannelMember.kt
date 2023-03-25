package dev.baseio.slackserver.communications

import dev.baseio.slackserver.data.models.SKUserPushToken
import dev.baseio.slackserver.data.models.SkChannelMember
import dev.baseio.slackserver.data.models.SkUser
import dev.baseio.slackserver.data.sources.UserPushTokenDataSource
import dev.baseio.slackserver.data.sources.UsersDataSource

class PNChannelMember(
    private val userPushTokenDataSource: UserPushTokenDataSource,
    private val usersDataSource: UsersDataSource
) : PNSender<SkChannelMember>() {
    override suspend fun getSender(senderUserId: String, request: SkChannelMember): SkUser? {
        return usersDataSource.getUser(senderUserId, request.workspaceId)
    }

    override suspend fun getPushTokens(request: SkChannelMember): List<SKUserPushToken> {
        return userPushTokenDataSource.getPushTokensFor(listOf(request.memberId))
    }
}