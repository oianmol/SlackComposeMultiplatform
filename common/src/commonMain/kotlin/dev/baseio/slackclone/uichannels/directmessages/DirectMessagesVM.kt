package dev.baseio.slackclone.uichannels.directmessages

import dev.baseio.slackclone.SlackViewModel
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchChannelsWithLastMessage
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapConcat

class DirectMessagesVM(
  private val useCaseFetchChannels: UseCaseFetchChannelsWithLastMessage,
  private val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace,
  coroutineDispatcherProvider: CoroutineDispatcherProvider
) : SlackViewModel(coroutineDispatcherProvider) {
  val channels = MutableStateFlow(fetchFlow())

  fun fetchFlow(): Flow<List<DomainLayerMessages.SKLastMessage>> {
    return useCaseGetSelectedWorkspace.invokeFlow().flatMapConcat {
      useCaseFetchChannels(it!!.uuid)
    }
  }
}