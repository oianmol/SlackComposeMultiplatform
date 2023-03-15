package dev.baseio.slackclone

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import dev.baseio.slackclone.channels.createsearch.CreateNewChannelComponent
import dev.baseio.slackclone.channels.createsearch.SearchChannelsComponent
import dev.baseio.slackclone.chatmessaging.newchat.NewChatThreadComponent
import dev.baseio.slackclone.dashboard.vm.DashboardComponent
import dev.baseio.slackclone.onboarding.AuthorizeTokenComponent
import dev.baseio.slackclone.onboarding.GettingStartedComponent
import dev.baseio.slackclone.onboarding.vm.EmailMagicLinkComponent
import dev.baseio.slackclone.qrscanner.QrScannerMode
import dev.baseio.slackdomain.AUTH_TOKEN
import dev.baseio.slackdomain.datasources.local.SKLocalKeyValueSource

interface Root {
    val childStack: Value<ChildStack<*, Child>>

    fun navigateDashboard()

    fun navigateQRScanner(mode: QrScannerMode)
    fun navigatePush(config: RootComponent.Config)
    fun navigationPop()
    fun navigationClear()
    fun navigateChannel(channelId: String, workspaceId: String)

    sealed class Child {
        data class GettingStarted(val component: GettingStartedComponent) : Child()
        data class AuthorizeWithToken(val component: AuthorizeTokenComponent) : Child()
        data class SearchCreateChannel(val component: SearchChannelsComponent) : Child()
        data class CreateNewChannel(val component: CreateNewChannelComponent) : Child()
        data class NewChatThread(val component: NewChatThreadComponent) : Child()
        data class DashboardScreen(val component: DashboardComponent) : Child()
        data class AuthorizeSendEmail(val component: EmailMagicLinkComponent) : Child()
        data class QrScanner(val mode: QrScannerMode) : Child()
        object EmailMagicLink : Child()
        data class SignInManually(val emailAddress: String) : Child()
    }

    fun navigateEmailMagicLink()
    fun navigateAuthorizeWithToken(token: String)
}

class RootComponent(
    context: ComponentContext,
    skKeyValueData: SKLocalKeyValueSource = getKoin().get()
) : Root, ComponentContext by context {

    private val navigation = StackNavigation<Config>()

    override val childStack: Value<ChildStack<*, Root.Child>> = childStack(
        source = navigation,
        initialConfiguration = skKeyValueData.get(AUTH_TOKEN)?.let {
            Config.DashboardScreen()
        } ?: run {
            Config.GettingStarted(firstLaunch = true)
        },
        handleBackButton = true, // Pop the back stack on back button press
        childFactory = ::createChild
    )

    override fun navigationPop() {
        navigation.pop()
    }

    override fun navigationClear() {
        navigation.popWhile { true }
    }

    override fun navigateChannel(channelId: String, workspaceId: String) {
        navigation.push(Config.DashboardScreen(channelId, workspaceId))
    }

    override fun navigateAuthorizeWithToken(token: String) {
        navigation.push(Config.AuthorizeWithToken(token))
    }

    override fun navigateDashboard() {
        navigation.navigate {
            listOf(Config.DashboardScreen())
        }
    }

    override fun navigateEmailMagicLink() {
        navigation.push(Config.EmailMagicLink)
    }

    override fun navigateQRScanner(mode: QrScannerMode) {
        navigation.push(Config.QrScanner(mode))
    }

    override fun navigatePush(config: Config) {
        navigation.push(config)
    }

    private fun createChild(config: Config, componentContext: ComponentContext): Root.Child =
        when (config) {
            is Config.AuthorizeWithToken -> Root.Child.AuthorizeWithToken(
                AuthorizeTokenComponent(
                    componentContext = componentContext.childContext(AuthorizeTokenComponent::class.qualifiedName.toString()),
                    token = config.token, navigateBack = ::navigationPop, navigateDashboard = ::navigateDashboard
                )
            )

            is Config.AuthorizeSendEmail -> Root.Child.AuthorizeSendEmail(
                EmailMagicLinkComponent(
                    componentContext = componentContext.childContext(EmailMagicLinkComponent::class.qualifiedName.toString()),
                    email = config.emailAddress,
                    workspace = config.workspace, ::navigateDashboard, ::navigationPop
                )
            )

            is Config.GettingStarted -> Root.Child.GettingStarted(
                GettingStartedComponent(
                    componentContext = componentContext.childContext(GettingStartedComponent::class.qualifiedName.toString()),
                    firstRun = config.firstLaunch,
                    navigateBack = ::navigationPop,
                    navigateDashboard = ::navigateDashboard
                ) {
                    navigateEmailMagicLink()
                }
            )

            is Config.DashboardScreen -> Root.Child.DashboardScreen(
                DashboardComponent(
                    componentContext = componentContext.childContext(DashboardComponent::class.qualifiedName.toString()),
                    navigateOnboarding = {
                        navigation.navigate {
                            listOf(Config.GettingStarted(firstLaunch = true))
                        }
                    }, navigateQrScanner = {
                        navigation.push(Config.QrScanner(it))
                    },
                    navigateRoot = {
                        navigation.push(it)
                    }, navigateAddWorkspace = {
                        navigation.push(Config.EmailMagicLink)
                    }
                ).also { dashboardComponent ->
                    config.channelId?.let { channelId ->
                        dashboardComponent.navigateChannel(
                            channelId
                        )
                    }
                }
            )

            Config.CreateNewChannelUI -> Root.Child.CreateNewChannel(
                CreateNewChannelComponent(
                    componentContext.childContext(CreateNewChannelComponent::class.qualifiedName.toString()),
                    {
                        navigation.pop()
                    },
                    { channel ->
                        navigation.popWhile {
                            it !is Config.DashboardScreen
                        }
                        (childStack.value.active.instance as? Root.Child.CreateNewChannel)?.component?.onChannelSelected(
                            channel
                        )
                        (childStack.value.active.instance as? Root.Child.DashboardScreen)?.component?.onChannelSelected(
                            channel
                        )
                    }
                )
            )

            Config.NewChatThreadScreen -> Root.Child.NewChatThread(
                NewChatThreadComponent(
                    componentContext.childContext(NewChatThreadComponent::class.qualifiedName.toString()),
                    {
                        navigation.pop()
                    },
                    { channel ->
                        navigation.popWhile {
                            it !is Config.DashboardScreen
                        }
                        (childStack.value.active.instance as? Root.Child.DashboardScreen)?.component?.onChannelSelected(
                            channel
                        )
                    }
                )
            )

            Config.SearchCreateChannelUI -> Root.Child.SearchCreateChannel(
                SearchChannelsComponent(
                    componentContext.childContext(SearchChannelsComponent::class.qualifiedName.toString()),
                    {
                        navigation.pop()
                    },
                    { config1 ->
                        navigation.push(config1)
                    },
                    { channel ->
                        navigation.popWhile {
                            it !is Config.DashboardScreen
                        }
                        (childStack.value.active.instance as? Root.Child.DashboardScreen)?.component?.onChannelSelected(
                            channel
                        )
                    }
                )
            )

            is Config.QrScanner -> Root.Child.QrScanner(mode = config.mode)
            Config.EmailMagicLink -> Root.Child.EmailMagicLink
            is Config.SignInManually -> Root.Child.SignInManually(config.workspace)
        }


    sealed class Config : Parcelable {

        @Parcelize
        data class QrScanner(val mode: QrScannerMode) : Config()

        @Parcelize
        data class GettingStarted(val firstLaunch: Boolean = true) : Config()

        @Parcelize
        data class DashboardScreen(val channelId: String? = null, val workspaceId: String? = null) :
            Config()

        @Parcelize
        object EmailMagicLink : Config()

        @Parcelize
        data class SignInManually(val workspace: String) : Config()

        @Parcelize
        data class AuthorizeSendEmail(val emailAddress: String, val workspace: String) : Config()

        @Parcelize
        data class AuthorizeWithToken(val token: String) : Config()

        @Parcelize
        object SearchCreateChannelUI : Config()

        @Parcelize
        object CreateNewChannelUI : Config()

        @Parcelize
        object NewChatThreadScreen : Config()
    }
}
