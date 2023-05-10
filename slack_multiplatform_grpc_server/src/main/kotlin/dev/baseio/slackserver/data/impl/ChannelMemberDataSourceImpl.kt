package dev.baseio.slackserver.data.impl

import dev.baseio.slackserver.data.models.SkChannel
import dev.baseio.slackserver.data.models.SkChannelMember
import dev.baseio.slackserver.data.sources.ChannelMemberDataSource
import org.bson.Document
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.match

class ChannelMemberDataSourceImpl(private val database: CoroutineDatabase) : ChannelMemberDataSource {

    override suspend fun isMember(userId: String, workspaceId: String, channelId: String): Boolean {
        return database.getCollection<SkChannelMember>()
            .find(
                SkChannelMember::workspaceId eq workspaceId,
                SkChannelMember::channelId eq channelId,
                SkChannelMember::memberId eq userId
            ).toList().isNotEmpty()
    }

    override suspend fun getChannelIdsForUserAndWorkspace(userId: String, workspaceId: String): List<String> {
        return database.getCollection<SkChannelMember>()
            .find(
                SkChannelMember::workspaceId eq workspaceId,
                SkChannelMember::memberId eq userId
            ).toList().map {
                it.channelId
            }
    }

    override suspend fun getMembers(workspaceId: String, channelId: String): List<SkChannelMember> {
        return database.getCollection<SkChannelMember>()
            .find(SkChannelMember::channelId eq channelId, SkChannelMember::workspaceId eq workspaceId)
            .toList()
    }

    override suspend fun addMembers(listOf: List<SkChannelMember>) {
        listOf.forEach { skChannelMember ->
            val channelMember = SkChannelMember(
                channelId = skChannelMember.channelId, memberId = skChannelMember.memberId, workspaceId = skChannelMember.workspaceId,
                channelEncryptedPrivateKey = skChannelMember.channelEncryptedPrivateKey
            )
            val memberCollection = database.getCollection<SkChannelMember>()
            memberCollection.findOne(
                SkChannelMember::channelId eq skChannelMember.channelId, SkChannelMember::memberId eq skChannelMember.memberId
            )?.let { existingChannelMember ->
                database.getCollection<SkChannelMember>()
                    .updateOne(
                        match(
                            Document.parse("{'fullDocument.memberId': '${existingChannelMember.memberId}'}"),
                            Document.parse("{'fullDocument.channelId': '${existingChannelMember.channelId}'}"),
                            Document.parse("{'fullDocument.workspaceId': '${existingChannelMember.workspaceId}'}"),
                        ),
                        channelMember
                    )
            } ?: kotlin.run {
                database.getCollection<SkChannelMember>()
                    .insertOne(channelMember)
            }
        }
    }
}
