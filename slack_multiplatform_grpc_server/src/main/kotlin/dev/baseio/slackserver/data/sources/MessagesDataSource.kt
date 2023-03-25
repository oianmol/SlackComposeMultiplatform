package dev.baseio.slackserver.data.sources

import dev.baseio.slackdata.protos.SKWorkspaceChannelRequest
import dev.baseio.slackserver.data.models.SkMessage
import kotlinx.coroutines.flow.Flow

interface MessagesDataSource {
  suspend fun saveMessage(request: SkMessage): SkMessage
  suspend fun getMessages(workspaceId: String, channelId: String, limit: Int, offset: Int): List<SkMessage>
  fun registerForChanges(request: SKWorkspaceChannelRequest): Flow<Pair<SkMessage?, SkMessage?>>
  suspend fun updateMessage(request: SkMessage): SkMessage?
  suspend fun getMessage(uuid: String, workspaceId: String): SkMessage?
}

