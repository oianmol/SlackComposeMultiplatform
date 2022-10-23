package dev.baseio.slackclone.uidashboard.vm

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import dev.baseio.slackclone.RootComponent
import dev.baseio.slackclone.koinApp
import dev.baseio.slackclone.uichannels.SlackChannelComponent
import dev.baseio.slackclone.uichannels.directmessages.DirectMessagesComponent
import dev.baseio.slackclone.uichat.chatthread.ChatScreenComponent
import dev.baseio.slackclone.uidashboard.home.HomeScreenComponent
import dev.baseio.slackclone.uidashboard.home.SearchMessagesComponent
import dev.baseio.slackclone.uidashboard.home.UserProfileComponent
import dev.baseio.slackdomain.model.channel.DomainLayerChannels


interface Dashboard {
  fun navigate(child: DashboardComponent.Config)
  fun onChannelSelected(channel: DomainLayerChannels.SKChannel)

  val phoneStack: Value<ChildStack<*, Child>>
  val desktopStack: Value<ChildStack<*, Child>>

  sealed class Child {
    data class HomeScreen(val component: HomeScreenComponent) : Child()
    data class DirectMessagesScreen(val component: DirectMessagesComponent) : Child()
    object MentionsScreen : Child()
    data class SearchScreen(val searchMessagesComponent: SearchMessagesComponent) : Child()
    data class UserProfileScreen(val component: UserProfileComponent) : Child()
  }
}


class DashboardComponent(
  componentContext: ComponentContext,
  val navigateOnboarding: () -> Unit,
  val navigateRoot: (RootComponent.Config) -> Unit
) : Dashboard, ComponentContext by componentContext {

  val sideNavComponent = SideNavComponent(this, koinApp.koin.get(), koinApp.koin.get(), koinApp.koin.get()) {
    navigateOnboarding()
  }
  val chatScreenComponent = ChatScreenComponent(
    componentContext
  )
  val recentChannelsComponent =
    SlackChannelComponent(this, "recent")
  val allChannelsComponent =
    SlackChannelComponent(this, "allChannels")


  val dashboardVM = instanceKeeper.getOrCreate {
    DashboardVM(
      koinApp.koin.get(),
      koinApp.koin.get(),
      koinApp.koin.get(),
      koinApp.koin.get(),
      koinApp.koin.get(),
      koinApp.koin.get(),
      koinApp.koin.get(),
      koinApp.koin.get(),
      koinApp.koin.get(),
      koinApp.koin.get()
    )
  }


  private val navigation = StackNavigation<Config>()


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
    Config.MentionsConfig -> Dashboard.Child.MentionsScreen
    Config.Profile -> Dashboard.Child.UserProfileScreen(
      UserProfileComponent(
        componentContext
      ) {
        navigateOnboarding()
      }
    )

    Config.Search -> Dashboard.Child.SearchScreen(SearchMessagesComponent(componentContext))
  }


  override fun onChannelSelected(channel: DomainLayerChannels.SKChannel) {
    chatScreenComponent.chatViewModel.requestFetch(channel)
    dashboardVM.onChannelSelected(channel)
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