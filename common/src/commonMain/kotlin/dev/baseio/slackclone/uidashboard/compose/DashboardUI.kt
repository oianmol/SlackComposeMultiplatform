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
import dev.baseio.slackclone.uichat.chatthread.ChatScreenUI
import dev.baseio.slackclone.uichat.chatthread.ChatScreenComponent
import dev.baseio.slackclone.uidashboard.compose.layouts.SlackDesktopLayout
import dev.baseio.slackclone.uidashboard.compose.layouts.SlackSideBarLayoutDesktop
import dev.baseio.slackclone.uidashboard.compose.layouts.SlackWorkspaceLayoutDesktop
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.active
import com.arkivanov.essenty.backhandler.BackCallback
import dev.baseio.slackclone.RootComponent
import dev.baseio.slackclone.uidashboard.home.*
import dev.baseio.slackclone.uidashboard.vm.Dashboard
import dev.baseio.slackclone.uidashboard.vm.DashboardComponent
import dev.baseio.slackclone.uionboarding.compose.PlatformSideEffects


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DashboardUI(
  dashboardComponent: DashboardComponent,
  chatScreenComponent: ChatScreenComponent
) {
  val scaffoldState = rememberScaffoldState()

  val colors = SlackCloneColorProvider.colors
  PlatformSideEffects.PlatformColors(colors.appBarColor, colors.uiBackground)


  val keyboardController = LocalSoftwareKeyboardController.current
  val lastChannel by dashboardComponent.selectedChatChannel.collectAsState(mainDispatcher)

  var isLeftNavOpen by remember { mutableStateOf(false) }
  val isChatViewClosed by dashboardComponent.isChatViewClosed.collectAsState(mainDispatcher)
  val size = getWindowSizeClass(LocalWindow.current)
  val screenWidth = LocalWindow.current.width
  val sideNavWidth = screenWidth * 0.8f
  val sideNavPxValue = with(LocalDensity.current) { sideNavWidth.toPx() }
  val screenWidthPxValue = with(LocalDensity.current) { screenWidth.toPx() }

  dashboardComponent.backHandler.register(BackCallback(!isChatViewClosed) {
    dashboardComponent.isChatViewClosed.value = true
  })


  LaunchedEffect(isChatViewClosed) {
    if (isChatViewClosed) {
      keyboardController?.hide()
    }
  }


  BoxWithConstraints {
    when (size) {
      WindowSize.Phones, WindowSize.SmallTablets -> {
        SlackDragComposableView(
          isLeftNavOpen = isLeftNavOpen,
          isChatViewClosed = checkChatViewClosed(lastChannel, isChatViewClosed),
          mainScreenOffset = sideNavPxValue,
          chatScreenOffset = screenWidthPxValue,
          onOpenCloseLeftView = {
            isLeftNavOpen = it
          },
          onOpenCloseRightView = {
            dashboardComponent.isChatViewClosed.value = it
          },
          leftViewComposable = { sideNavModifier ->
            SideNavigation(
              modifier = sideNavModifier.width(sideNavWidth),
              viewModel = dashboardComponent.sideNavComponent,
              {
                isLeftNavOpen = false
              }, {
                dashboardComponent.navigateOnboarding()
              }
            )
          },
          rightViewComposable = { chatViewModifier ->
            lastChannel?.let { slackChannel ->
              ChatScreenUI(
                modifier = chatViewModifier,
                onBackClick = { dashboardComponent.isChatViewClosed.value = true },
                viewModel = chatScreenComponent
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
              dashboardComponent.selectedChatChannel.value = it
              chatScreenComponent.requestFetch(it)
              dashboardComponent.isChatViewClosed.value = false
            }, dashboardComponent
          )
        }
      }

      WindowSize.BigTablets, WindowSize.DesktopOne -> {
        SlackDualPaneLayoutView(
          leftViewComposable = {
            SideNavigation(
              modifier = it,
              viewModel = dashboardComponent.sideNavComponent,
              {
                isLeftNavOpen = false
              }, {
                dashboardComponent.navigateOnboarding()
              }
            )
          },
          rightViewComposable = { chatViewModifier ->
            lastChannel?.let { slackChannel ->
              ChatScreenUI(
                modifier = chatViewModifier,
                onBackClick = {
                  dashboardComponent.isChatViewClosed.value = true
                  dashboardComponent.selectedChatChannel.value = null
                },
                viewModel = chatScreenComponent
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
              dashboardComponent.selectedChatChannel.value = it
              chatScreenComponent.requestFetch(it)
              dashboardComponent.isChatViewClosed.value = false
            },
            dashboardComponent = dashboardComponent,
          )
        }

      }

      else -> {
        val onItemClick = { channel: Any ->
          dashboardComponent.selectedChatChannel.value = channel as DomainLayerChannels.SKChannel
          chatScreenComponent.requestFetch(channel)
          dashboardComponent.isChatViewClosed.value = false
        }
        val clearChat = {
          dashboardComponent.isChatViewClosed.value = true
          dashboardComponent.selectedChatChannel.value = null
        }
        SlackDesktopLayout(modifier = Modifier.fillMaxSize(), sideBar = { modifier ->
          SlackSideBarLayoutDesktop(modifier, dashboardComponent.sideNavComponent, openDM = {
            clearChat()
            dashboardComponent.navigate(DashboardComponent.Config.DirectMessages)
          }, mentionsScreen = {
            clearChat()
            dashboardComponent.navigate(DashboardComponent.Config.MentionsConfig)
          }, searchScreen = {
            clearChat()
            dashboardComponent.navigate(DashboardComponent.Config.Search)
          }, userProfile = {
            clearChat()
            dashboardComponent.navigate(DashboardComponent.Config.Profile)
          }, dashboardComponent)
        }, workSpaceAndChannels = { modifier ->
          SlackWorkspaceLayoutDesktop(modifier, onItemClick = { skChannel ->
            onItemClick(skChannel)
          }, onCreateChannelRequest = {
            dashboardComponent.navigateRoot(RootComponent.Config.SearchCreateChannelUI)
          }, dashboardComponent.recentChannelsComponent, dashboardComponent.allChannelsComponent,dashboardComponent)
        }) { contentModifier ->
          lastChannel?.let { slackChannel ->
            ChatScreenUI(
              modifier = contentModifier,
              onBackClick = {
                dashboardComponent.isChatViewClosed.value = true
                dashboardComponent.selectedChatChannel.value = null
              },
              viewModel = chatScreenComponent
            )
          } ?: run {
            SlackCloneSurface(
              color = SlackCloneColorProvider.colors.uiBackground,
              modifier = contentModifier
            ) {
              Children(stack = dashboardComponent.desktopStack, animation = stackAnimation(fade())) {
                when (val child = it.instance) {
                  is Dashboard.Child.DirectMessagesScreen -> DirectMessagesUI(
                    onItemClick = onItemClick,
                    child.component
                  )

                  is Dashboard.Child.MentionsScreen -> MentionsReactionsUI(child.mentionsComponent)
                  is Dashboard.Child.SearchScreen -> SearchMessagesUI(child.searchMessagesComponent)
                  is Dashboard.Child.UserProfileScreen -> {
                    UserProfileUI(child.component)
                  }

                  is Dashboard.Child.HomeScreen -> TODO()
                }
              }
            }
          }
        }
      }
    }
  }

}

enum class WindowSize { Phones, SmallTablets, BigTablets, DesktopOne, DesktopTwo }

fun getWindowSizeClass(windowDpSize: WindowInfo): WindowSize = when {
  windowDpSize.width < 0.dp ->
    throw IllegalArgumentException("Dp value cannot be negative")

  windowDpSize.width < 600.dp -> WindowSize.Phones
  windowDpSize.width < 960.dp -> WindowSize.SmallTablets
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
  dashboardComponent: DashboardComponent,
) {
  Box(modifier) {
    Scaffold(
      backgroundColor = SlackCloneColorProvider.colors.uiBackground,
      contentColor = SlackCloneColorProvider.colors.textSecondary,
      modifier = Modifier,
      scaffoldState = scaffoldState,
      bottomBar = {
        DashboardBottomNavBar(dashboardComponent)
      },
      snackbarHost = {
        scaffoldState.snackbarHostState
      },
      floatingActionButton = {
        FloatingDM {
          dashboardComponent.navigateRoot(RootComponent.Config.NewChatThreadScreen)
        }
      }
    ) { innerPadding ->
      Box(modifier = Modifier.padding(innerPadding)) {
        SlackCloneSurface(
          color = SlackCloneColorProvider.colors.uiBackground,
          modifier = Modifier.fillMaxSize()
        ) {
          DashboardChildren(
            modifier,
            dashboardComponent,
            appBarIconClick,
            onItemClick
          )
        }
      }
      if (needsOverlay) {
        OverlayDark(appBarIconClick)
      }
    }
  }
}

@OptIn(ExperimentalDecomposeApi::class)
@Composable
private fun DashboardChildren(
  modifier: Modifier,
  dashboardComponent: DashboardComponent,
  appBarIconClick: () -> Unit,
  onItemClick: (DomainLayerChannels.SKChannel) -> Unit
) {
  Children(modifier = modifier, stack = dashboardComponent.phoneStack, animation = stackAnimation(fade())) {
    when (val child = it.instance) {
      is Dashboard.Child.HomeScreen -> {
        HomeScreenUI(
          child.component,
          appBarIconClick,
          onItemClick = onItemClick,
          onCreateChannelRequest = {
            dashboardComponent.navigateRoot(RootComponent.Config.SearchCreateChannelUI)
          }, dashboardComponent.recentChannelsComponent, dashboardComponent.allChannelsComponent
        )
      }

      is Dashboard.Child.DirectMessagesScreen -> DirectMessagesUI(onItemClick = onItemClick, child.component)
      is Dashboard.Child.MentionsScreen -> MentionsReactionsUI(child.mentionsComponent)
      is Dashboard.Child.SearchScreen -> SearchMessagesUI(child.searchMessagesComponent)
      is Dashboard.Child.UserProfileScreen -> {
        UserProfileUI(child.component)
      }
    }
  }
}

@Composable
fun FloatingDM(onClick: () -> Unit) {
  FloatingActionButton(onClick = {
    onClick()
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
fun DashboardBottomNavBar(dashboardComponent: DashboardComponent) {
  Column(Modifier.background(color = SlackCloneColorProvider.colors.uiBackground)) {
    Divider(
      color = SlackCloneColorProvider.colors.textPrimary.copy(alpha = 0.2f),
      thickness = 0.5.dp
    )
    BottomNavigation(backgroundColor = SlackCloneColorProvider.colors.uiBackground) {
      val navBackStackEntry = dashboardComponent.phoneStack.active.instance
      val dashTabs = mutableListOf(
        DashboardComponent.Config.Home,
        DashboardComponent.Config.DirectMessages,
        DashboardComponent.Config.Search,
        DashboardComponent.Config.MentionsConfig,
        DashboardComponent.Config.Profile
      )
      dashTabs.forEach { screen ->
        BottomNavItem(screen, navBackStackEntry, dashboardComponent)
      }
    }
  }
}

@Composable
private fun RowScope.BottomNavItem(
  screen: DashboardComponent.Config,
  navBackStackEntry: Dashboard.Child,
  dashboardComponent: DashboardComponent
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
    selected = dashboardComponent.phoneStack.active == navBackStackEntry,
    onClick = {
      dashboardComponent.navigate(screen)
    }
  )
}

