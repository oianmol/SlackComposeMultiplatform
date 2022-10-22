package dev.baseio.slackclone.uidashboard.vm

import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import dev.baseio.slackdomain.usecases.workspaces.UseCaseGetSelectedWorkspace
import kotlinx.coroutines.flow.*
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import dev.baseio.grpc.GrpcCalls
import dev.baseio.slackclone.RootComponent
import dev.baseio.slackclone.koinApp
import dev.baseio.slackclone.uichannels.SlackChannelComponent
import dev.baseio.slackclone.uichannels.directmessages.DirectMessagesComponent
import dev.baseio.slackclone.uichat.chatthread.ChatScreenComponent
import dev.baseio.slackclone.uidashboard.home.HomeScreenComponent
import dev.baseio.slackclone.uidashboard.home.MentionsComponent
import dev.baseio.slackclone.uidashboard.home.SearchMessagesComponent
import dev.baseio.slackclone.uidashboard.home.UserProfileComponent
import dev.baseio.slackclone.uionboarding.coroutineScope
import dev.baseio.slackdata.SKKeyValueData
import dev.baseio.slackdata.datasources.local.channels.skUser
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchAndSaveChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseFetchAndUpdateChangeInChannels
import dev.baseio.slackdomain.usecases.channels.UseCaseWorkspaceChannelRequest
import dev.baseio.slackdomain.usecases.chat.UseCaseFetchAndUpdateChangeInMessages
import dev.baseio.slackdomain.usecases.users.UseCaseFetchAndSaveUsers
import dev.baseio.slackdomain.usecases.users.UseCaseFetchAndUpdateChangeInUsers
import dev.baseio.slackdomain.usecases.workspaces.UseCaseFetchAndSaveWorkspaces
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


interface Dashboard {
  fun navigate(child: DashboardComponent.Config)
  fun onChannelSelected(channel: DomainLayerChannels.SKChannel)

  val phoneStack: Value<ChildStack<*, Child>>
  val desktopStack: Value<ChildStack<*, Child>>

  sealed class Child {
    data class HomeScreen(val component: HomeScreenComponent) : Child()
    data class DirectMessagesScreen(val component: DirectMessagesComponent) : Child()
    data class MentionsScreen(val mentionsComponent: MentionsComponent) : Child()
    data class SearchScreen(val searchMessagesComponent: SearchMessagesComponent) : Child()
    data class UserProfileScreen(val component: UserProfileComponent) : Child()
  }
}


class DashboardComponent(
  componentContext: ComponentContext,
  useCaseFetchAndSaveWorkspaces: UseCaseFetchAndSaveWorkspaces,
  coroutineDispatcherProvider: CoroutineDispatcherProvider,
  private val useCaseObserveMessages: UseCaseFetchAndUpdateChangeInMessages,
  private val useCaseObserveUsers: UseCaseFetchAndUpdateChangeInUsers,
  private val useCaseObserveChannels: UseCaseFetchAndUpdateChangeInChannels,
  private val useCaseGetSelectedWorkspace: UseCaseGetSelectedWorkspace,
  private val useCaseFetchChannels: UseCaseFetchAndSaveChannels,
  private val useCaseFetchAndSaveUsers: UseCaseFetchAndSaveUsers,
  private val skKeyValueData: SKKeyValueData,
  private val grpcCalls: GrpcCalls,
  val navigateOnboarding: () -> Unit,
  val navigateRoot: (RootComponent.Config) -> Unit
) : Dashboard, ComponentContext by componentContext {


  private val viewModelScope = coroutineScope(coroutineDispatcherProvider.main + SupervisorJob())

  val selectedChatChannel = MutableStateFlow<DomainLayerChannels.SKChannel?>(null)
  var selectedWorkspace = MutableStateFlow<DomainLayerWorkspaces.SKWorkspace?>(null)
  val isChatViewClosed = MutableStateFlow(true)


  private var observeNewMessagesJob: Job? = null
  private var useCaseObserveUsersJob: Job? = null
  private var useCaseObserveChannelsJob: Job? = null
  var lastSelectedWorkspace = MutableStateFlow(flow())
    private set

  var lastWorkspace: String? = null
  fun flow() = useCaseGetSelectedWorkspace.invokeFlow()

  init {
    lastSelectedWorkspace.value.onEach { workspace ->
      workspace?.uuid?.let { workspaceId ->
        cancelJobIfWorkspaceChanged(workspaceId)
        lastWorkspace = workspaceId
        val user = skKeyValueData.skUser()// TODO is this the best way to fetch user ?
        observeForUserData(workspaceId)
        viewModelScope.launch {
          useCaseFetchChannels.invoke(workspaceId, 0, 20)
          useCaseFetchAndSaveUsers(workspaceId)
        }
        grpcCalls.listenToChangeInChannelMembers(workspaceId, skKeyValueData.skUser().uuid).map {
          useCaseFetchChannels.invoke(workspaceId, 0, 20)
        }.launchIn(viewModelScope)
      }
    }.launchIn(viewModelScope)

    viewModelScope.launch {
      useCaseFetchAndSaveWorkspaces.invoke()
    }
    useCaseGetSelectedWorkspace.invokeFlow().onEach {
      if (selectedWorkspace.value != it) {
        selectedChatChannel.value = null
        isChatViewClosed.value = true
      }
      selectedWorkspace.value = it
    }.launchIn(viewModelScope)
  }


  private val navigation = StackNavigation<Config>()

  val sideNavComponent = SideNavComponent(this, koinApp.koin.get(), koinApp.koin.get(), koinApp.koin.get()) {
    navigateOnboarding()
  }
  val chatScreenComponent = ChatScreenComponent(
    componentContext, koinApp.koin.get(),
    koinApp.koin.get(),
    koinApp.koin.get(),
    koinApp.koin.get(),
    koinApp.koin.get(),
    koinApp.koin.get(),
  )
  val recentChannelsComponent =
    SlackChannelComponent(this, koinApp.koin.get(), koinApp.koin.get(), koinApp.koin.get())
  val allChannelsComponent =
    SlackChannelComponent(this, koinApp.koin.get(), koinApp.koin.get(), koinApp.koin.get())

  private val phoneChildStack = childStack(
    key = "phone",
    source = navigation,
    initialConfiguration = Config.Home,
    handleBackButton = true, // Pop the back stack on back button press
    childFactory = ::createChild,
  )

  private val desktopChildStack = childStack(
    key = "desktop",
    source = navigation,
    initialConfiguration = Config.DirectMessages,
    handleBackButton = true, // Pop the back stack on back button press
    childFactory = ::createChild,
  )


  override val phoneStack: Value<ChildStack<*, Dashboard.Child>> = phoneChildStack
  override val desktopStack: Value<ChildStack<*, Dashboard.Child>> = desktopChildStack


  override fun navigate(child: Config) {
    navigation.replaceCurrent(child)
  }

  private fun createChild(config: Config, componentContext: ComponentContext): Dashboard.Child = when (config) {
    Config.DirectMessages -> Dashboard.Child.DirectMessagesScreen(
      DirectMessagesComponent(
        koinApp.koin.get(), koinApp.koin.get(), componentContext
      )
    )

    Config.Home -> Dashboard.Child.HomeScreen(HomeScreenComponent(componentContext, koinApp.koin.get()))
    Config.MentionsConfig -> Dashboard.Child.MentionsScreen(MentionsComponent(componentContext))
    Config.Profile -> Dashboard.Child.UserProfileScreen(
      UserProfileComponent(
        koinApp.koin.get(), koinApp.koin.get(), componentContext
      ) {
        navigateOnboarding()
      }
    )

    Config.Search -> Dashboard.Child.SearchScreen(SearchMessagesComponent(componentContext))
  }


  override fun onChannelSelected(channel: DomainLayerChannels.SKChannel) {
    selectedChatChannel.value = channel
    chatScreenComponent.requestFetch(channel)
    isChatViewClosed.value = false
  }

  private fun observeForUserData(workspaceId: String) {
    observeNewMessagesJob =
      useCaseObserveMessages.invoke(UseCaseWorkspaceChannelRequest(workspaceId = workspaceId)).launchIn(viewModelScope)
    useCaseObserveUsersJob = useCaseObserveUsers.invoke(workspaceId).launchIn(viewModelScope)
    useCaseObserveChannelsJob = useCaseObserveChannels.invoke(workspaceId).launchIn(viewModelScope)
  }

  private fun cancelJobIfWorkspaceChanged(workspaceId: String) {
    lastWorkspace?.let { lastWorkspace ->
      if (lastWorkspace != workspaceId) {
        observeNewMessagesJob?.cancel()
        useCaseObserveUsersJob?.cancel()
        useCaseObserveChannelsJob?.cancel()
      }
    }
  }

  sealed class Config(val name: String) : Parcelable {
    @Parcelize
    object Home : Config("Home")

    @Parcelize
    object DirectMessages : Config("DMs")

    @Parcelize
    object MentionsConfig : Config("Mentions")

    @Parcelize
    object Search : Config("Search")

    @Parcelize
    object Profile : Config("You")
  }

}