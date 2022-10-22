package dev.baseio.slackclone

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import dev.baseio.slackclone.uichannels.createsearch.CreateNewChannelComponent
import dev.baseio.slackclone.uichannels.createsearch.SearchChannelsComponent
import dev.baseio.slackclone.uichat.chatthread.ChatScreenComponent
import dev.baseio.slackclone.uichat.newchat.NewChatThreadComponent
import dev.baseio.slackclone.uidashboard.vm.DashboardComponent
import dev.baseio.slackclone.uionboarding.GettingStartedComponent
import dev.baseio.slackclone.uionboarding.vm.CreateWorkspaceComponent
import dev.baseio.slackdata.SKKeyValueData
import dev.baseio.slackdomain.AUTH_TOKEN

interface Root {
  val childStack: Value<ChildStack<*, Child>>

  fun navigateCreateWorkspace(isLogin: Boolean)
  fun navigateDashboard()
  fun navigatePush(config: RootComponent.Config)
  fun navigationPop()

  sealed class Child {
    data class GettingStarted(val component: GettingStartedComponent) : Child()
    data class SearchCreateChannel(val component: SearchChannelsComponent) : Child()
    data class CreateNewChannel(val component: CreateNewChannelComponent) : Child()
    data class NewChatThread(val component: NewChatThreadComponent) : Child()
    data class CreateWorkspace(val component: CreateWorkspaceComponent) : Child()
    data class DashboardScreen(val component: DashboardComponent) : Child()
  }
}

class RootComponent(
  context: ComponentContext,
  skKeyValueData: SKKeyValueData
) : Root, ComponentContext by context {

  private val navigation = StackNavigation<Config>()

  override val childStack: Value<ChildStack<*, Root.Child>> = childStack(
    source = navigation,
    initialConfiguration = skKeyValueData.get(AUTH_TOKEN)?.let {
      Config.DashboardScreen
    } ?: run {
      Config.GettingStarted
    },
    handleBackButton = true, // Pop the back stack on back button press
    childFactory = ::createChild,
  )

  override fun navigationPop() {
    navigation.pop()
  }

  override fun navigateCreateWorkspace(isLogin: Boolean) {
    navigation.push(Config.CreateWorkspace(isLogin))
  }

  override fun navigateDashboard() {
    navigation.replaceCurrent(Config.DashboardScreen)
  }

  override fun navigatePush(config: Config) {
    navigation.push(config)
  }

  private fun createChild(config: Config, componentContext: ComponentContext): Root.Child =
    when (config) {
      is Config.GettingStarted -> Root.Child.GettingStarted(
        GettingStartedComponent(
          componentContext,
          koinApp.koin.get()
        ) { isLogin ->
          navigateCreateWorkspace(isLogin)
        }
      )

      is Config.CreateWorkspace -> Root.Child.CreateWorkspace(
        CreateWorkspaceComponent(
          componentContext,
          koinApp.koin.get(),
          koinApp.koin.get(),
          config.isLogin
        ) {
          navigateDashboard()
        }
      )

      Config.DashboardScreen -> Root.Child.DashboardScreen(
        DashboardComponent(
          componentContext,
          koinApp.koin.get(),
          koinApp.koin.get(),
          koinApp.koin.get(),
          koinApp.koin.get(),
          koinApp.koin.get(),
          koinApp.koin.get(),
          koinApp.koin.get(),
          koinApp.koin.get(),
          koinApp.koin.get(),
          koinApp.koin.get(), {
            navigation.bringToFront(Config.GettingStarted)
          }, {
            navigation.push(it)
          }
        )
      )

      Config.CreateNewChannelUI -> Root.Child.CreateNewChannel(
        CreateNewChannelComponent(
          componentContext, koinApp.koin.get(),
          koinApp.koin.get(), koinApp.koin.get(),
          {
            navigation.pop()
          }, { channel ->
            navigation.popWhile {
              it != Config.DashboardScreen
            }
            (childStack.value.active.instance as? Root.Child.DashboardScreen)?.component?.onChannelSelected(channel)
          })
      )

      Config.NewChatThreadScreen -> Root.Child.NewChatThread(
        NewChatThreadComponent(
          componentContext,
          koinApp.koin.get(),
          koinApp.koin.get(),
          koinApp.koin.get(),
          koinApp.koin.get(),
          koinApp.koin.get(),
          {
            navigation.pop()
          }, { channel ->
            navigation.popWhile {
              it != Config.DashboardScreen
            }
            (childStack.value.active.instance as? Root.Child.DashboardScreen)?.component?.onChannelSelected(channel)
          }
        )
      )

      Config.SearchCreateChannelUI -> Root.Child.SearchCreateChannel(
        SearchChannelsComponent(
          componentContext, koinApp.koin.get(),
          koinApp.koin.get(),
          koinApp.koin.get(),
          koinApp.koin.get(),
          {
            navigation.pop()
          }, { config1 ->
            navigation.push(config1)
          }, { channel ->
            navigation.popWhile {
              it != Config.DashboardScreen
            }
            (childStack.value.active.instance as? Root.Child.DashboardScreen)?.component?.onChannelSelected(channel)
          }
        ))
    }


  sealed class Config : Parcelable {

    @Parcelize
    object GettingStarted : Config()

    @Parcelize
    object DashboardScreen : Config()

    @Parcelize
    data class CreateWorkspace(var isLogin: Boolean) : Config()

    @Parcelize
    object SearchCreateChannelUI : Config()

    @Parcelize
    object CreateNewChannelUI : Config()

    @Parcelize
    object NewChatThreadScreen : Config()
  }
}