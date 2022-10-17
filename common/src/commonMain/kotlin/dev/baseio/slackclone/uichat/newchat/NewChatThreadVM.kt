package dev.baseio.slackclone.uichat.newchat

import ViewModel
import dev.baseio.slackclone.navigation.ComposeNavigator
import dev.baseio.slackclone.navigation.NavigationKey
import dev.baseio.slackclone.navigation.SlackScreens
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseCreateChannel
import dev.baseio.slackdomain.usecases.channels.UseCaseWorkspaceChannelRequest
import dev.baseio.slackdomain.usecases.channels.UseCaseSearchChannel
import dev.baseio.slackdomain.usecases.users.UseCaseFetchAndSaveUsers
import dev.baseio.slackdomain.usecases.users.UseCaseFetchLocalUsers
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class NewChatThreadVM(
  private val ucFetchLocalChannels: UseCaseSearchChannel,
  private val useCaseFetchLocalUsers: UseCaseFetchLocalUsers,
  private val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace,
  private val useCaseFetchAndSaveUsers: UseCaseFetchAndSaveUsers,
  private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
  private val useCaseCreateChannel: UseCaseCreateChannel
) :
  ViewModel() {

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
        useCaseGetSelectedWorkspace.invokeFlow().flatMapConcat { workspace ->
          combine(
            useCaseFetchLocalUsers(workspace!!.uuid, search).map {
              it.map { skUser ->
                DomainLayerChannels.SKChannel.SkDMChannel(
                  workId = workspace.uuid,
                  senderId = "",
                  receiverId = skUser.uuid,
                  uuid = "",
                  deleted = false
                ).apply {
                  channelName = skUser.name
                  pictureUrl = skUser.avatarUrl
                }
              }
            }, ucFetchLocalChannels(
              UseCaseWorkspaceChannelRequest(workspaceId = workspace.uuid, search)
            )
          ) { first, second ->
            return@combine first + second
          }
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

  private fun navigate(channel: DomainLayerChannels.SKChannel, composeNavigator: ComposeNavigator) {
    composeNavigator.deliverResult(
      NavigationKey.NavigateChannel,
      channel,
      SlackScreens.Dashboard
    )
  }

  fun createChannel(channel: DomainLayerChannels.SKChannel, composeNavigator: ComposeNavigator) {
    viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
      errorStream.value = throwable
    }) {
      val result = useCaseCreateChannel.invoke(channel)
      val channelNew = result.getOrThrow()
      navigate(channelNew, composeNavigator)
    }

  }
}