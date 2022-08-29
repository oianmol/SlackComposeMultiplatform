package dev.baseio.slackclone.navigation

class SlackScreens {
  object GettingStarted : BackstackScreen("gettingStarted")
  object SkipTypingScreen : BackstackScreen("SkipTypingUI")
  object EmailAddressInputUI : BackstackScreen("EmailAddressInputUI")
  object WorkspaceInputUI : BackstackScreen("WorkspaceInputUI")

  // DashboardNavigation
  object DashboardNavigation : BackstackScreen("DashboardNavigation")
  object Dashboard : BackstackScreen("Dashboard")
  object CreateChannelsScreen : BackstackScreen("CreateChannelsScreen")
  object CreateNewChannel : BackstackScreen("CreateNewChannel")
  object CreateNewDM : BackstackScreen("CreateNewDM")
  object Home : BackstackScreen("Home")
  object DMs : BackstackScreen("DMs")
  object Mentions :
    BackstackScreen("Mentions")
  object Search :
    BackstackScreen("Search")
  object You : BackstackScreen("You")
}