package dev.baseio.slackclone.uichat.newchat

import ViewModel
import com.arkivanov.decompose.ComponentContext
import dev.baseio.slackclone.uionboarding.coroutineScope
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseCreateChannel
import dev.baseio.slackdomain.usecases.users.UseCaseFetchAndSaveUsers
import dev.baseio.slackdomain.usecases.users.UseCaseFetchChannelsWithSearch
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NewChatThreadComponent(
  componentContext: ComponentContext,
  private val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace,
  private val useCaseFetchAndSaveUsers: UseCaseFetchAndSaveUsers,
  private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
  private val useCaseCreateChannel: UseCaseCreateChannel,
  private val useCaseFetchChannelsWithSearch: UseCaseFetchChannelsWithSearch,
  val navigationPop: () -> Unit
) : ComponentContext by componentContext {

  private val viewModelScope = coroutineScope(coroutineDispatcherProvider.main + SupervisorJob())

  val search = MutableStateFlow("")
  var channelsStream = MutableStateFlow<List<DomainLayerChannels.SKChannel>>(emptyList())
    private set

  var errorStream = MutableStateFlow<Throwable?>(null)
    private set

  init {
    viewModelScope.launch {
      useCaseGetSelectedWorkspace.invokeFlow().onEach { workspace ->
        workspace?.uuid?.let { useCaseFetchAndSaveUsers(it) }
      }.launchIn(this)

      search.collectLatest { search ->
        useCaseGetSelectedWorkspace.invokeFlow()
          .mapNotNull { it }
          .flatMapConcat { workspace ->
            useCaseFetchChannelsWithSearch(workspace.uuid, search)
          }.flowOn(coroutineDispatcherProvider.io)
          .onEach {
            channelsStream.value = it
          }.flowOn(coroutineDispatcherProvider.main)
          .launchIn(viewModelScope)
      }
    }
  }

  fun search(newValue: String) {
    search.value = newValue
  }

  private fun navigate(channel: DomainLayerChannels.SKChannel) {
    TODO()
    /* composeNavigator.deliverResult(
       NavigationKey.NavigateChannel,
       channel,
       SlackScreens.Dashboard
     )*/
  }

  fun createChannel(channel: DomainLayerChannels.SKChannel) {
    viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
      errorStream.value = throwable
    }) {
      channel.channelId.takeIf { it.isNotEmpty() }?.let {
        navigate(channel)
      } ?: run {
        val result = useCaseCreateChannel.invoke(channel)
        val channelNew = result.getOrThrow()
        navigate(channelNew)
      }
    }

  }
}