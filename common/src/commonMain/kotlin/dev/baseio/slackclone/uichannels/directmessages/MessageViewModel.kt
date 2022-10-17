package dev.baseio.slackclone.uichannels.directmessages

import dev.baseio.slackdomain.model.message.DomainLayerMessages
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchChannelsWithLastMessage
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import kotlinx.coroutines.flow.*
import ViewModel
class MessageViewModel constructor(
  private val useCaseFetchChannels: UseCaseFetchChannelsWithLastMessage,
  private val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace
) : ViewModel() {

  val channels = MutableStateFlow(fetchFlow())

  fun fetchFlow(): Flow<List<DomainLayerMessages.SKLastMessage>> {
    return useCaseGetSelectedWorkspace.invokeFlow().flatMapConcat {
      useCaseFetchChannels(it!!.uuid)
    }
  }

}