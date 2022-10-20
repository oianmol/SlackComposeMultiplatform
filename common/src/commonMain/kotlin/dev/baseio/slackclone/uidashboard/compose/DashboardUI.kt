package dev.baseio.slackclone.uidashboard.compose

import mainDispatcher
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.LocalWindow
import dev.baseio.slackclone.WindowInfo
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackclone.commonui.reusable.SlackDragComposableView
import dev.baseio.slackclone.commonui.theme.SlackCloneColor
import dev.baseio.slackclone.commonui.theme.SlackCloneColorProvider
import dev.baseio.slackclone.commonui.theme.SlackCloneSurface
import dev.baseio.slackclone.commonui.theme.SlackCloneTypography
import dev.baseio.slackclone.navigation.*
import dev.baseio.slackclone.uichat.chatthread.ChatScreenUI
import dev.baseio.slackclone.uichat.chatthread.ChatScreenVM
import dev.baseio.slackclone.uidashboard.compose.layouts.SlackDesktopLayout
import dev.baseio.slackclone.uidashboard.compose.layouts.SlackSideBarLayoutDesktop
import dev.baseio.slackclone.uidashboard.compose.layouts.SlackWorkspaceLayoutDesktop
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import dev.baseio.slackclone.uidashboard.home.*
import dev.baseio.slackclone.uidashboard.vm.DashboardVM
import dev.baseio.slackclone.uionboarding.compose.PlatformSideEffects

val homeNavigator = SlackComposeNavigator()

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BackstackScreen.DashboardUI(
  composeNavigator: ComposeNavigator,
  dashboardVM: DashboardVM,
  viewModel: ChatScreenVM
) {
  val scaffoldState = rememberScaffoldState()

  val colors = SlackCloneColorProvider.colors
  PlatformSideEffects.PlatformColors(colors.appBarColor, colors.uiBackground)


  val keyboardController = LocalSoftwareKeyboardController.current
  val lastChannel by dashboardVM.selectedChatChannel.collectAsState(mainDispatcher)

  var isLeftNavOpen by remember { mutableStateOf(false) }
  val isChatViewClosed by dashboardVM.isChatViewClosed.collectAsState(mainDispatcher)
  val size = getWindowSizeClass(LocalWindow.current)
  val screenWidth = LocalWindow.current.width
  val sideNavWidth = screenWidth * 0.8f
  val sideNavPxValue = with(LocalDensity.current) { sideNavWidth.toPx() }
  val screenWidthPxValue = with(LocalDensity.current) { screenWidth.toPx() }

  if (!isChatViewClosed) {
    composeNavigator.observeWhenBackPressedFor(SlackScreens.Dashboard) {
      if (!isChatViewClosed) {
        dashboardVM.isChatViewClosed.value = true
      }
    }
  } else {
    composeNavigator.removeObserverForBackPress(SlackScreens.Dashboard)
  }

  LaunchedEffect(isChatViewClosed) {
    if (isChatViewClosed) {
      keyboardController?.hide()
    }
  }


  BoxWithConstraints {
    when (size) {
      WindowSize.Phones, WindowSize.Tablets -> {
        SlackDragComposableView(
          isLeftNavOpen = isLeftNavOpen,
          isChatViewClosed = checkChatViewClosed(lastChannel, isChatViewClosed),
          mainScreenOffset = sideNavPxValue,
          chatScreenOffset = screenWidthPxValue,
          onOpenCloseLeftView = {
            isLeftNavOpen = it
          },
          onOpenCloseRightView = {
            dashboardVM.isChatViewClosed.value = it
          },
          leftViewComposable = { sideNavModifier ->
            SideNavigation(
              modifier = sideNavModifier.width(sideNavWidth),
              composeNavigator = composeNavigator,
              scope.get()
            ) {
              isLeftNavOpen = false
            }
          },
          rightViewComposable = { chatViewModifier ->
            lastChannel?.let { slackChannel ->
              ChatScreenUI(
                modifier = chatViewModifier,
                SKChannel = slackChannel,
                onBackClick = { dashboardVM.isChatViewClosed.value = true },
                viewModel = viewModel
              )
            }
          }
        ) { mainViewModifier ->
          DashboardScaffold(
            needsOverlay = isLeftNavOpen || isChatViewClosed.not(),
            scaffoldState = scaffoldState,
            modifier = mainViewModifier,
            appBarIconClick = { isLeftNavOpen = isLeftNavOpen.not() },
            onItemClick = {
              dashboardVM.selectedChatChannel.value = it
              viewModel.requestFetch(it)
              dashboardVM.isChatViewClosed.value = false
            }, composeNavigator
          )
        }
      }

      WindowSize.BigTablets, WindowSize.DesktopOne -> {
        SlackDualPaneLayoutView(
          leftViewComposable = {
            SideNavigation(
              modifier = it,
              composeNavigator = composeNavigator,
              scope.get()
            ) {
              isLeftNavOpen = false
            }
          },
          rightViewComposable = { chatViewModifier ->
            lastChannel?.let { slackChannel ->
              ChatScreenUI(
                modifier = chatViewModifier,
                SKChannel = slackChannel,
                onBackClick = {
                  dashboardVM.isChatViewClosed.value = true
                  dashboardVM.selectedChatChannel.value = null
                },
                viewModel = viewModel
              )
            }
          },
        ) { modifier ->
          DashboardScaffold(
            needsOverlay = false,
            scaffoldState = scaffoldState,
            modifier = modifier,
            appBarIconClick = { isLeftNavOpen = isLeftNavOpen.not() },
            onItemClick = {
              dashboardVM.selectedChatChannel.value = it
              viewModel.requestFetch(it)
              dashboardVM.isChatViewClosed.value = false
            },
            composeNavigator = composeNavigator,
          )
        }

      }

      else -> {
        val onItemClick = { channel: Any ->
          dashboardVM.selectedChatChannel.value = channel as DomainLayerChannels.SKChannel
          viewModel.requestFetch(channel)
          dashboardVM.isChatViewClosed.value = false
        }
        val clearChat = {
          dashboardVM.isChatViewClosed.value = true
          dashboardVM.selectedChatChannel.value = null
        }
        SlackDesktopLayout(modifier = Modifier.fillMaxSize(), sideBar = { modifier ->
          SlackSideBarLayoutDesktop(modifier, scope.get(), openDM = {
            clearChat()
            homeNavigator.navigateScreen(SlackScreens.DMs)
          }, mentionsScreen = {
            clearChat()
            homeNavigator.navigateScreen(SlackScreens.Mentions)
          }, searchScreen = {
            clearChat()
            homeNavigator.navigateScreen(SlackScreens.Search)
          }, userProfile = {
            clearChat()
            homeNavigator.navigateScreen(SlackScreens.You)
          }, homeNavigator)
        }, workSpaceAndChannels = { modifier ->
          SlackWorkspaceLayoutDesktop(modifier, onItemClick = { skChannel ->
            onItemClick(skChannel)
          }, onCreateChannelRequest = {
            composeNavigator.registerForNavigationResult(
              NavigationKey.NavigateChannel,
              SlackScreens.Dashboard
            ) {
              onItemClick(it as DomainLayerChannels.SKChannel)
            }
            composeNavigator.navigateScreen(SlackScreens.CreateChannelsScreen)
          }, composeNavigator)
        }) { contentModifier ->
          lastChannel?.let { slackChannel ->
            ChatScreenUI(
              modifier = contentModifier,
              SKChannel = slackChannel,
              onBackClick = {
                dashboardVM.isChatViewClosed.value = true
                dashboardVM.selectedChatChannel.value = null
              },
              viewModel = viewModel
            )
          } ?: run {
            SlackCloneSurface(
              color = SlackCloneColorProvider.colors.uiBackground,
              modifier = contentModifier
            ) {
              Navigator(homeNavigator, initialRoute = SlackScreens.DesktopHomeRoute) {
                this.route(SlackScreens.DesktopHomeRoute) {
                  screen(SlackScreens.DMs) {
                    DirectMessagesUI(onItemClick = onItemClick, scope.get())
                  }
                  screen(SlackScreens.Mentions) {
                    MentionsReactionsUI()
                  }
                  screen(SlackScreens.Search) {
                    SearchMessagesUI()
                  }
                  screen(SlackScreens.You) {
                    val gettingStartedVM = scope.get<UserProfileVM>()
                    UserProfileUI(gettingStartedVM)
                  }
                }
              }
            }
          }
        }
      }
    }
  }

}

enum class WindowSize { Phones, Tablets, BigTablets, DesktopOne, DesktopTwo }

fun getWindowSizeClass(windowDpSize: WindowInfo): WindowSize = when {
  windowDpSize.width < 0.dp ->
    throw IllegalArgumentException("Dp value cannot be negative")

  windowDpSize.width < 600.dp -> WindowSize.Phones
  windowDpSize.width < 960.dp -> WindowSize.Tablets
  windowDpSize.width < 1024.dp -> WindowSize.BigTablets
  windowDpSize.width < 1366.dp -> WindowSize.DesktopOne
  else -> WindowSize.DesktopTwo
}

@Composable
private fun SlackDualPaneLayoutView(
  leftViewComposable: @Composable (Modifier) -> Unit,
  rightViewComposable: @Composable (Modifier) -> Unit,
  mainContent: @Composable (Modifier) -> Unit
) {
  Row {
    leftViewComposable(Modifier.weight(1f))
    Box(Modifier.weight(3f)) {
      mainContent(Modifier)
      rightViewComposable(Modifier)
    }
  }

}

private fun checkChatViewClosed(
  lastChannel: DomainLayerChannels.SKChannel?,
  isChatViewClosed: Boolean
) = lastChannel == null || isChatViewClosed

@Composable
private fun DashboardScaffold(
  needsOverlay: Boolean,
  scaffoldState: ScaffoldState,
  modifier: Modifier,
  appBarIconClick: () -> Unit,
  onItemClick: (DomainLayerChannels.SKChannel) -> Unit,
  composeNavigator: ComposeNavigator,
) {
  Box(modifier) {
    Scaffold(
      backgroundColor = SlackCloneColorProvider.colors.uiBackground,
      contentColor = SlackCloneColorProvider.colors.textSecondary,
      modifier = Modifier,
      scaffoldState = scaffoldState,
      bottomBar = {
        DashboardBottomNavBar(homeNavigator)
      },
      snackbarHost = {
        scaffoldState.snackbarHostState
      },
      floatingActionButton = {
        FloatingDM(composeNavigator, onItemClick)
      }
    ) { innerPadding ->
      Box(modifier = Modifier.padding(innerPadding)) {
        SlackCloneSurface(
          color = SlackCloneColorProvider.colors.uiBackground,
          modifier = Modifier.fillMaxSize()
        ) {
          Navigator(homeNavigator, initialRoute = SlackScreens.HomeRoute) {
            this.route(SlackScreens.HomeRoute) {
              screen(SlackScreens.Home) {
                HomeScreenUI(
                  appBarIconClick,
                  onItemClick = onItemClick,
                  onCreateChannelRequest = {
                    composeNavigator.registerForNavigationResult(
                      NavigationKey.NavigateChannel,
                      SlackScreens.Dashboard
                    ) {
                      onItemClick(it as DomainLayerChannels.SKChannel)
                    }
                    composeNavigator.navigateScreen(SlackScreens.CreateChannelsScreen)
                  })
              }
              screen(SlackScreens.DMs) {
                DirectMessagesUI(onItemClick = onItemClick, scope.get())
              }
              screen(SlackScreens.Mentions) {
                MentionsReactionsUI()
              }
              screen(SlackScreens.Search) {
                SearchMessagesUI()
              }
              screen(SlackScreens.You) {
                val gettingStartedVM = scope.get<UserProfileVM>()
                UserProfileUI(gettingStartedVM)
              }
            }
          }
        }
      }
      if (needsOverlay) {
        OverlayDark(appBarIconClick)
      }
    }
  }
}

@Composable
fun FloatingDM(composeNavigator: ComposeNavigator, onItemClick: (DomainLayerChannels.SKChannel) -> Unit) {
  FloatingActionButton(onClick = {
    composeNavigator.registerForNavigationResult(NavigationKey.NavigateChannel, SlackScreens.Dashboard) {
      composeNavigator.navigateUp()
      onItemClick(it as DomainLayerChannels.SKChannel)
    }
    composeNavigator.navigateScreen(SlackScreens.CreateNewDM)
  }, backgroundColor = Color.White) {
    Icon(
      imageVector = Icons.Default.Edit,
      contentDescription = null,
      tint = SlackCloneColor
    )
  }
}

@Composable
private fun OverlayDark(appBarIconClick: () -> Unit) {
  Box(
    Modifier
      .fillMaxSize()
      .clickable {
        appBarIconClick()
      }
      .background(Color.Black.copy(alpha = 0.4f))
  ) {

  }
}

@Composable
fun DashboardBottomNavBar(navController: ComposeNavigator?) {
  Column(Modifier.background(color = SlackCloneColorProvider.colors.uiBackground)) {
    Divider(
      color = SlackCloneColorProvider.colors.textPrimary.copy(alpha = 0.2f),
      thickness = 0.5.dp
    )
    BottomNavigation(backgroundColor = SlackCloneColorProvider.colors.uiBackground) {
      val navBackStackEntry = navController?.lastScreen
      val dashTabs = getDashTabs()
      dashTabs.forEach { screen ->
        BottomNavItem(screen, navBackStackEntry, navController)
      }
    }
  }
}

@Composable
private fun RowScope.BottomNavItem(
  screen: BackstackScreen,
  currentDestination: BackstackScreen?,
  navController: ComposeNavigator?,
) {

  BottomNavigationItem(
    selectedContentColor = SlackCloneColorProvider.colors.bottomNavSelectedColor,
    unselectedContentColor = SlackCloneColorProvider.colors.bottomNavUnSelectedColor,
    icon = { Icon(Icons.Default.Home, contentDescription = null, Modifier.size(24.dp)) },
    label = {
      Text(
        screen.name,
        maxLines = 1,
        style = SlackCloneTypography.overline,
      )
    },
    selected = currentDestination == screen,
    onClick = {
      navController?.navigateScreen(screen)
    }
  )
}

private fun getDashTabs(): MutableList<BackstackScreen> {
  return mutableListOf<BackstackScreen>().apply {
    add(SlackScreens.Home)
    add(SlackScreens.DMs)
    add(SlackScreens.Mentions)
    add(SlackScreens.Search)
    add(SlackScreens.You)
  }
}

