package dev.baseio.slackserver.data.impl

import com.mongodb.client.model.Filters
import com.mongodb.client.model.changestream.OperationType
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

  override suspend fun isChannelExistFor(sender: String, receiver: String): SkChannel? {
    val possible1 = database.getCollection<SkChannelMember>()
      .findOne(SkChannelMember::channelId eq sender + receiver)
    val possible2 = database.getCollection<SkChannelMember>()
      .findOne(SkChannelMember::channelId eq receiver + sender)
    possible1?.let { skChannelMember ->
      return skChannel(skChannelMember)
    }
    possible2?.let { skChannelMember ->
      return skChannel(skChannelMember)
    }
    return null

  }

  private suspend fun skChannel(skChannelMember: SkChannelMember) =
    database.getCollection<SkChannel.SkDMChannel>()
      .findOne(SkChannel.SkDMChannel::uuid eq skChannelMember.channelId)
      ?: database.getCollection<SkChannel.SkGroupChannel>()
        .findOne(SkChannel.SkGroupChannel::uuid eq skChannelMember.channelId)

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
            ), channelMember
          )
      } ?: kotlin.run {
        database.getCollection<SkChannelMember>()
          .insertOne(channelMember)
      }
    }
  }

}