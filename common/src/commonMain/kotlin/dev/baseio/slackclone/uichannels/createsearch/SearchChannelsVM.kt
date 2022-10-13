package dev.baseio.slackclone.uichannels.createsearch

import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackdomain.mappers.UiModelMapper
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseChannelRequest
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchChannelCount
import dev.baseio.slackdomain.usecases.channels.UseCaseSearchChannel
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ViewModel
class SearchChannelsVM constructor(
  private val ucFetchChannels: UseCaseSearchChannel,
  private val useCaseFetchChannelCount: UseCaseFetchChannelCount,
  private val chatPresentationMapper: UiModelMapper<DomainLayerChannels.SKChannel, UiLayerChannels.SKChannel>,
  private val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace
) : ViewModel() {

  val search = MutableStateFlow("")
  val channelCount = MutableStateFlow(0)

  var channels = MutableStateFlow(flow(""))

  init {
    viewModelScope.launch {
      val currentSelectedWorkspaceId = useCaseGetSelectedWorkspace.perform()
      currentSelectedWorkspaceId?.let {
        val count = useCaseFetchChannelCount.perform(workspaceId = currentSelectedWorkspaceId.uuid)
        channelCount.value = count
      }
    }
  }

  private fun flow(search: String) =
    useCaseGetSelectedWorkspace.performStreaming(Unit).flatMapConcat {
      ucFetchChannels.performStreaming(UseCaseChannelRequest(it!!.uuid, search)).map { channels ->
        channels.map { channel ->
          chatPresentationMapper.mapToPresentation(channel)
        }
      }
    }

  fun search(newValue: String) {
    search.value = newValue
    channels.value = flow(newValue)
  }

}