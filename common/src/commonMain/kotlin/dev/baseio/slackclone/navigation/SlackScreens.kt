package dev.baseio.slackclone.navigation

class SlackScreens {

  object OnboardingRoute : BackstackRoute("OnboardingRoute", GettingStarted)
  object GettingStarted : BackstackScreen("gettingStarted")
  object SkipTypingScreen : BackstackScreen("SkipTypingUI")
  object EmailAddressInputUI : BackstackScreen("EmailAddressInputUI")
  object WorkspaceInputUI : BackstackScreen("WorkspaceInputUI")

  // DashboardNavigation
  object DashboardRoute : BackstackRoute("DashboardNavigation", Dashboard)
  object Dashboard : BackstackScreen("Dashboard")
  object CreateChannelsScreen : BackstackScreen("CreateChannelsScreen")
  object CreateNewChannel : BackstackScreen("CreateNewChannel")
  object CreateNewDM : BackstackScreen("CreateNewDM")

  object HomeRoute : BackstackRoute("HomeRoute", initialScreen = Home)
  object Home : BackstackScreen("Home")
  object DMs : BackstackScreen("DMs")
  object Mentions :
    BackstackScreen("Mentions")

  object Search :
    BackstackScreen("Search")

  object You : BackstackScreen("You")
}

sealed class NavigationKey(val key:String) {
  object NavigateChannel : NavigationKey("navigateChannel")
}