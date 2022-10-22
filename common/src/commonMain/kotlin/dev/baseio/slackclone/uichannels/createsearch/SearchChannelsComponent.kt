package dev.baseio.slackclone.uichannels.createsearch

import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseWorkspaceChannelRequest
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchChannelCount
import dev.baseio.slackdomain.usecases.channels.UseCaseSearchChannel
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import kotlinx.coroutines.launch
import ViewModel
import com.arkivanov.decompose.ComponentContext
import dev.baseio.slackclone.uionboarding.coroutineScope
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlin.time.Duration.Companion.milliseconds

class SearchChannelsComponent constructor(
  componentContext:ComponentContext,
  private val ucFetchChannels: UseCaseSearchChannel,
  private val useCaseFetchChannelCount: UseCaseFetchChannelCount,
  private val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace,
  private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : ComponentContext by componentContext{
  private val viewModelScope = coroutineScope(coroutineDispatcherProvider.main + SupervisorJob())

  val search = MutableStateFlow("")
  val channelCount = MutableStateFlow(0)
  var channels = MutableStateFlow<List<DomainLayerChannels.SKChannel>>(emptyList())

  init {
    viewModelScope.launch {
      val currentSelectedWorkspaceId = useCaseGetSelectedWorkspace()
      currentSelectedWorkspaceId?.let {
        val count = useCaseFetchChannelCount(workspaceId = currentSelectedWorkspaceId.uuid)
        channelCount.value = count
      }
      search.debounce(250.milliseconds).collectLatest { search ->
        useCaseGetSelectedWorkspace.invokeFlow().flatMapConcat {
          ucFetchChannels(UseCaseWorkspaceChannelRequest(it!!.uuid, search))
        }
          .flowOn(coroutineDispatcherProvider.io)
          .onEach {
            channels.value = it
          }.flowOn(coroutineDispatcherProvider.main)
          .launchIn(viewModelScope)
      }
    }

  }

}