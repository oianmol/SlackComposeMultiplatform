package dev.baseio.slackclone.domain.repository

import dev.baseio.slackclone.domain.model.message.DomainLayerMessages
import kotlinx.coroutines.flow.Flow

interface ChannelLastMessageRepository {
  fun fetchChannels(): Flow<List<DomainLayerMessages.LastMessage>>
}