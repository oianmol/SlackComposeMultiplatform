package dev.baseio.slackclone.navigation

import dev.baseio.slackclone.uichat.newchat.NewChatThreadVM
import org.koin.core.annotation.KoinInternalApi

class SlackScreens {

    object OnboardingRoute : BackstackRoute("OnboardingRoute", GettingStarted)
    object GettingStarted : BackstackScreen("gettingStarted")

    object CreateWorkspace : BackstackScreen("createWorkspace") {
        const val IS_LOGIN = "isLogin"
        fun withArgs(isLogin: Boolean): BackstackScreen {
            argMap[IS_LOGIN] = isLogin
            return this
        }
    }

    object SkipTypingScreen : BackstackScreen("SkipTypingUI")
    object EmailAddressInputUI : BackstackScreen("EmailAddressInputUI")
    object WorkspaceInputUI : BackstackScreen("WorkspaceInputUI")

    // DashboardNavigation
    object DashboardRoute : BackstackRoute("DashboardNavigation", Dashboard)
    object Dashboard : BackstackScreen("Dashboard")
    object CreateChannelsScreen : BackstackScreen("CreateChannelsScreen")
    object CreateNewChannel : BackstackScreen("CreateNewChannel")
    object CreateNewDM : BackstackScreen("CreateNewDM") {
        @OptIn(KoinInternalApi::class)
        override fun close() {
            scope.get<NewChatThreadVM>().onClear()
            super.close()
            val keys = scope.getKoin().instanceRegistry.instances.map {
                it.key
            }
            println(keys)
        }
    }

    object HomeRoute : BackstackRoute("HomeRoute", initialScreen = Home)

    object DesktopHomeRoute : BackstackRoute("DesktopHomeRoute", initialScreen = DMs)

    object Home : BackstackScreen("Home")
    object DMs : BackstackScreen("DMs")
    object Mentions : BackstackScreen("Mentions")
    object Search : BackstackScreen("Search")
    object You : BackstackScreen("You")
}

sealed class NavigationKey(val key: String) {
    object NavigateChannel : NavigationKey("navigateChannel")
}