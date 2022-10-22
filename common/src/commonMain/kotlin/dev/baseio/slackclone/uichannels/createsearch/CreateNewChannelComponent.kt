package dev.baseio.slackclone.uichannels.createsearch

import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseCreateChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import ViewModel
import com.arkivanov.decompose.ComponentContext
import dev.baseio.slackclone.uionboarding.coroutineScope
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.SupervisorJob
import kotlinx.datetime.Clock

class CreateNewChannelComponent constructor(
  componentContext: ComponentContext,
  coroutineDispatcherProvider: CoroutineDispatcherProvider,
  private val useCaseCreateChannel: UseCaseCreateChannel,
  private val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace,
  val navigationPop:()->Unit,
  val navigationWith:(DomainLayerChannels.SKChannel)->Unit
) : ComponentContext by componentContext {

  private val viewModelScope = coroutineScope(coroutineDispatcherProvider.main + SupervisorJob())


  var createChannelState =
    MutableStateFlow(
      DomainLayerChannels.SKChannel.SkGroupChannel(
        avatarUrl = null,
        workId = "",
        uuid = Clock.System.now().toEpochMilliseconds().toString(),
        name = "",
        createdDate = Clock.System.now().toEpochMilliseconds(),
        modifiedDate = Clock.System.now().toEpochMilliseconds(),
        deleted = false
      )
    )

  fun createChannel() {
    viewModelScope.launch(CoroutineExceptionHandler { coroutineContext, throwable ->
      throwable.printStackTrace()
    }) {
      if (createChannelState.value.name?.isNotEmpty() == true) {
        val lastSelectedWorkspace = useCaseGetSelectedWorkspace()
        lastSelectedWorkspace?.let {
          createChannelState.value = createChannelState.value.copy(
            workId = lastSelectedWorkspace.uuid,
            uuid = "${createChannelState.value.name}_${lastSelectedWorkspace.uuid}"
          )
          val channel = useCaseCreateChannel(createChannelState.value).getOrThrow()
          navigationWith(channel)
        }

      }
    }
  }
}