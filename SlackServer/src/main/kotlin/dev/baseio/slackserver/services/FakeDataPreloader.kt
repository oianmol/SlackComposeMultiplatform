package dev.baseio.slackserver.services

import dev.baseio.slackdata.protos.SKChannel
import dev.baseio.slackdata.protos.SKUser
import dev.baseio.slackdata.protos.SKWorkspace
import dev.baseio.slackserver.data.ChannelsDataSource
import dev.baseio.slackserver.data.MessagesDataSource
import dev.baseio.slackserver.data.UsersDataSource
import dev.baseio.slackserver.data.WorkspaceDataSource
import java.util.UUID

class FakeDataPreloader(
  private val workspaceDataSource: WorkspaceDataSource,
  private val channelsDataSource: ChannelsDataSource,
  private val messagesDataSource: MessagesDataSource,
  private val usersDataSource: UsersDataSource
) {

  val workSpacesLocal = getWorkSpaces()

  val users = buildUsers(workSpacesLocal)

  val channels = buildChannelsForWorkspaces(workSpacesLocal)

  private fun buildChannelsForWorkspaces(
    workSpaces: MutableList<SKWorkspace>
  ): MutableList<SKChannel> {
    val channels = mutableListOf<SKChannel>()
    workSpaces.forEach { skWorkspace ->
      // for every workspace create 50 channels
      repeat(5) {
        channels.add(
          SKChannel.newBuilder()
            .setWorkspaceId(skWorkspace.uuid)
            .setUuid(UUID.randomUUID().toString())
            .setName("Channel #$it")
            .setCreatedDate(System.currentTimeMillis())
            .setModifiedDate(System.currentTimeMillis())
            .setIsMuted(false)
            .setIsPrivate(false)
            .setIsStarred(false)
            .setIsShareOutSide(false)
            .setIsOneToOne(false)
            .setAvatarUrl("")
            .build()
        )
      }
    }
    return channels
  }

  private fun buildUsers(
    workSpaces: MutableList<SKWorkspace>,
  ): MutableList<SKUser> {
    val users = mutableListOf<SKUser>()
    workSpaces.forEach { skWorkspace ->
      users.addAll(getUsers(skWorkspace))
    }
    return users
  }

  private fun getUsers(skWorkspace: SKWorkspace): MutableList<SKUser> {
    return mutableListOf<SKUser>().apply {
      repeat(50) {
        add(
          SKUser.newBuilder().setUuid(
            UUID.randomUUID().toString()
          )
            .setWorkspaceId(skWorkspace.uuid)
            .setGender("Male")
            .setName("Emery O'Kon")
            .setEmail("emery.o'kon@email.com")
            .setUsername("emery.o'kon").setUserSince(System.currentTimeMillis())
            .setPhone(
              "+222 1-612-884-0086 x98654"
            ).setAvatarUrl(
              "https://robohash.org/etetsit.png?size=300x300&set=set1"
            ).build()
        )
      }
    }
  }


  private fun getWorkSpaces(): MutableList<SKWorkspace> {
    return mutableListOf<SKWorkspace>().apply {
      add(
        SKWorkspace.newBuilder()
          .setUuid(UUID.randomUUID().toString())
          .setName("Kotlin")
          .setDomain("kotlinlang.slack.com")
          .setPicUrl("https://avatars.slack-edge.com/2021-08-18/2394702857843_51119ca847fe3f05614b_88.png")
          .build()
      )
      add(
        SKWorkspace.newBuilder()
          .setUuid(UUID.randomUUID().toString())
          .setName("mutualmobile")
          .setDomain("mutualmobile.slack.com")
          .setPicUrl("https://avatars.slack-edge.com/2018-07-20/401750958992_1b07bb3c946bc863bfc6_88.png")
          .build()
      )
      add(
        SKWorkspace.newBuilder()
          .setUuid(UUID.randomUUID().toString())
          .setName("androidworldwide")
          .setDomain("androidworldwide.slack.com")
          .setPicUrl(
            "https://avatars.slack-edge.com/2020-09-03/1337922760453_3531ceb03787e9a60507_88.png",
          )
          .build()
      )
      add(
        SKWorkspace.newBuilder()
          .setUuid(UUID.randomUUID().toString())
          .setName("gophers")
          .setDomain("gophers.slack.com")
          .setPicUrl(
            "https://avatars.slack-edge.com/2019-12-06/850376190706_33364309961e71e9fe4a_88.png",
          )
          .build()
      )
    }
  }

  fun preload() {
    workSpacesLocal.map { skWorkspace ->
      workspaceDataSource.saveWorkspace(skWorkspace.toDBWorkspace(skWorkspace.uuid))
    }
    channels.map { channel ->
      channelsDataSource.insertChannel(channel.toDBChannel(channel.workspaceId, channel.uuid))
    }
    users.map { user ->
      usersDataSource.saveUser(user.toDBUser(user.uuid))
    }
  }
}