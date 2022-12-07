package dev.baseio.slackclone.dashboard.vm

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import dev.baseio.slackclone.RootComponent
import dev.baseio.slackclone.getKoin
import dev.baseio.slackclone.channels.SlackChannelComponent
import dev.baseio.slackclone.channels.directmessages.DirectMessagesComponent
import dev.baseio.slackclone.chatmessaging.chatthread.ChatScreenComponent
import dev.baseio.slackclone.dashboard.home.HomeScreenComponent
import dev.baseio.slackclone.dashboard.home.UserProfileComponent
import dev.baseio.slackclone.qrscanner.QrScannerMode
import dev.baseio.slackdomain.model.channel.DomainLayerChannels

class SearchMessagesComponent(componentContext: ComponentContext) : ComponentContext by componentContext

interface Dashboard {
    fun navigate(child: DashboardComponent.Config)
    fun onChannelSelected(channel: DomainLayerChannels.SKChannel)
    fun navigateChannel(channelId: String)

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
    val navigateQrScanner:(QrScannerMode)->Unit,
    val navigateRoot: (RootComponent.Config) -> Unit,
    val navigateAddWorkspace:()->Unit
) : Dashboard, ComponentContext by componentContext {

    val sideNavComponent = SideNavComponent(childContext(SideNavComponent::class.qualifiedName.toString())) {
        navigateOnboarding()
    }
    val chatScreenComponent = ChatScreenComponent(
        childContext(ChatScreenComponent::class.qualifiedName.toString())
    )
    val recentChannelsComponent =
        SlackChannelComponent(
            childContext(SlackChannelComponent::class.qualifiedName.toString().plus("recent")),
            "recent"
        )
    val allChannelsComponent =
        SlackChannelComponent(
            childContext(SlackChannelComponent::class.qualifiedName.toString().plus("allChannels")),
            "allChannels"
        )

    val dashboardVM = instanceKeeper.getOrCreate {
        DashboardVM(
            getKoin().get(),
            getKoin().get(),
            getKoin().get(),
            getKoin().get(),
            getKoin().get(),
            getKoin().get(),
            getKoin().get(),
            getKoin().get(),
            getKoin().get(),
            getKoin().get()
        )
    }

    private val navigation = StackNavigation<Config>()

    private val phoneChildStack = childStack(
        key = "phone",
        source = navigation,
        initialConfiguration = Config.Home,
        handleBackButton = true, // Pop the back stack on back button press
        childFactory = ::createChild
    )

    private val desktopChildStack = childStack(
        key = "desktop",
        source = navigation,
        initialConfiguration = Config.DirectMessages,
        handleBackButton = true, // Pop the back stack on back button press
        childFactory = ::createChild
    )

    override val phoneStack: Value<ChildStack<*, Dashboard.Child>> = phoneChildStack
    override val desktopStack: Value<ChildStack<*, Dashboard.Child>> = desktopChildStack

    override fun navigate(child: Config) {
        navigation.replaceCurrent(child)
    }

    private fun createChild(config: Config, componentContext: ComponentContext): Dashboard.Child = when (config) {
        Config.DirectMessages -> Dashboard.Child.DirectMessagesScreen(
            DirectMessagesComponent(componentContext.childContext(DirectMessagesComponent::class.qualifiedName.toString()))
        )

        Config.Home -> Dashboard.Child.HomeScreen(HomeScreenComponent(componentContext.childContext(HomeScreenComponent::class.qualifiedName.toString()), getKoin().get()))
        Config.MentionsConfig -> Dashboard.Child.MentionsScreen
        Config.Profile -> Dashboard.Child.UserProfileScreen(
            UserProfileComponent(
                componentContext.childContext(UserProfileComponent::class.qualifiedName.toString())
            ) {
                navigateOnboarding()
            }
        )

        Config.Search -> Dashboard.Child.SearchScreen(SearchMessagesComponent(componentContext.childContext(SearchMessagesComponent::class.qualifiedName.toString())))
    }

    override fun onChannelSelected(channel: DomainLayerChannels.SKChannel) {
        chatScreenComponent.chatViewModel.requestFetch(channel)
        dashboardVM.onChannelSelected(channel)
    }

    override fun navigateChannel(channelId: String) {
        chatScreenComponent.chatViewModel.requestFetch(channelId) { skChannel ->
            dashboardVM.onChannelSelected(skChannel)
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
