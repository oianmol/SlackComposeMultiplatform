package dev.baseio.slackdata.datasources.local

import dev.baseio.database.SlackDB
import dev.baseio.slackdomain.datasources.local.SKLocalDatabaseSource

class SKLocalDatabaseSourceImpl(private val slackDB: SlackDB) : SKLocalDatabaseSource {
  override fun clear() {
    with(slackDB.slackDBQueries){
      deleteAllMessages()
      deleteAllDMChannels()
      deleteMessages()
      deleteAllUsers()
      deleteSlackUser()
      deleteAllPublicChannels()
      deleteChannelMembers()
      deleteSlackWorkspaces()
      deleteAllMessages()
    }
  }
}