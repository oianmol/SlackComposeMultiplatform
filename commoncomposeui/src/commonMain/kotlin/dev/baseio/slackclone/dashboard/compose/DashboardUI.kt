package dev.baseio.slackclone.dashboard.compose

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.arkivanov.decompose.router.stack.active
import com.arkivanov.essenty.backhandler.BackCallback
import dev.baseio.slackclone.LocalWindow
import dev.baseio.slackclone.RootComponent
import dev.baseio.slackclone.WindowInfo
import dev.baseio.slackclone.commonui.reusable.SlackDragComposableView
import dev.baseio.slackclone.commonui.theme.SlackCloneColor
import dev.baseio.slackclone.commonui.theme.LocalSlackCloneColor
import dev.baseio.slackclone.commonui.theme.SlackCloneSurface
import dev.baseio.slackclone.commonui.theme.SlackCloneTypography
import dev.baseio.slackclone.chatmessaging.chatthread.ChatScreenComponent
import dev.baseio.slackclone.chatmessaging.chatthread.ChatScreenUI
import dev.baseio.slackclone.dashboard.compose.layouts.SlackDesktopLayout
import dev.baseio.slackclone.dashboard.compose.layouts.SlackSideBarLayoutDesktop
import dev.baseio.slackclone.dashboard.compose.layouts.SlackWorkspaceLayoutDesktop
import dev.baseio.slackclone.dashboard.home.*
import dev.baseio.slackclone.dashboard.vm.Dashboard
import dev.baseio.slackclone.dashboard.vm.DashboardComponent
import dev.baseio.slackclone.dashboard.vm.DashboardVM
import dev.baseio.slackclone.onboarding.compose.PlatformSideEffects
import dev.baseio.slackclone.qrscanner.QrScannerMode
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import mainDispatcher

@OptIn(ExperimentalComposeUiApi::class, ExperimentalDecomposeApi::class)
@Composable
internal fun DashboardUI(
    dashboardComponent: DashboardComponent,
    chatScreenComponent: ChatScreenComponent = dashboardComponent.chatScreenComponent,
    dashboardVM: DashboardVM = dashboardComponent.dashboardVM
) {
    val scaffoldState = rememberScaffoldState()

    val colors = LocalSlackCloneColor.current
    PlatformSideEffects.PlatformColors(colors.appBarColor, colors.uiBackground)

    val keyboardController = LocalSoftwareKeyboardController.current
    val lastChannel by dashboardVM.selectedChatChannel.collectAsState(mainDispatcher)
    val isChatViewClosed by dashboardVM.isChatViewClosed.collectAsState(mainDispatcher)

    var isLeftNavOpen by remember { mutableStateOf(false) }
    val size = getWindowSizeClass(LocalWindow.current)
    val screenWidth = LocalWindow.current.width
    val sideNavWidth = screenWidth * 0.8f
    val sideNavPxValue = with(LocalDensity.current) { sideNavWidth.toPx() }
    val screenWidthPxValue = with(LocalDensity.current) { screenWidth.toPx() }

    dashboardComponent.backHandler.register(
        BackCallback(!isChatViewClosed) {
            dashboardVM.isChatViewClosed.value = true
        }
    )

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
                        dashboardVM.isChatViewClosed.value = it
                    },
                    leftViewComposable = { sideNavModifier ->
                        SideNavigation(
                            modifier = sideNavModifier.width(sideNavWidth),
                            sideNavComponent = dashboardComponent.sideNavComponent,
                            {
                                isLeftNavOpen = false
                            },
                            {
                                dashboardComponent.navigateOnboarding()
                            }, {
                                dashboardComponent.navigateQrScanner(it)
                            }, {
                                dashboardComponent.navigateAddWorkspace()
                            }
                        )
                    },
                    rightViewComposable = { chatViewModifier ->
                        lastChannel?.let { slackChannel ->
                            ChatScreenUI(
                                modifier = chatViewModifier,
                                onBackClick = { dashboardVM.isChatViewClosed.value = true },
                                chatScreenComponent = chatScreenComponent,
                                slackChannel = slackChannel
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
                            chatScreenComponent.chatViewModel.requestFetch(it)
                            dashboardVM.isChatViewClosed.value = false
                        },
                        dashboardComponent
                    )
                }
            }

            WindowSize.BigTablets, WindowSize.DesktopOne -> {
                SlackDualPaneLayoutView(
                    leftViewComposable = {
                        SideNavigation(
                            modifier = it,
                            sideNavComponent = dashboardComponent.sideNavComponent,
                            {
                                isLeftNavOpen = false
                            },
                            {
                                dashboardComponent.navigateOnboarding()
                            }, {
                                dashboardComponent.navigateQrScanner(it)
                            }, {
                                dashboardComponent.navigateAddWorkspace()
                            }
                        )
                    },
                    rightViewComposable = { chatViewModifier ->
                        lastChannel?.let { slackChannel ->
                            ChatScreenUI(
                                modifier = chatViewModifier,
                                onBackClick = {
                                    dashboardVM.isChatViewClosed.value = true
                                    dashboardVM.selectedChatChannel.value = null
                                },
                                chatScreenComponent = chatScreenComponent,
                                slackChannel = slackChannel
                            )
                        }
                    }
                ) { modifier ->
                    DashboardScaffold(
                        needsOverlay = false,
                        scaffoldState = scaffoldState,
                        modifier = modifier,
                        appBarIconClick = { isLeftNavOpen = isLeftNavOpen.not() },
                        onItemClick = {
                            dashboardVM.selectedChatChannel.value = it
                            chatScreenComponent.chatViewModel.requestFetch(it)
                            dashboardVM.isChatViewClosed.value = false
                        },
                        dashboardComponent = dashboardComponent
                    )
                }
            }

            else -> {
                val onItemClick = { channel: Any ->
                    dashboardVM.selectedChatChannel.value = channel as DomainLayerChannels.SKChannel
                    chatScreenComponent.chatViewModel.requestFetch(channel)
                    dashboardVM.isChatViewClosed.value = false
                }
                val clearChat = {
                    dashboardVM.isChatViewClosed.value = true
                    dashboardVM.selectedChatChannel.value = null
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
                    }, qrCode = {
                        dashboardComponent.navigateQrScanner(QrScannerMode.QR_DISPLAY)
                    }, addWorkspace = {
                        dashboardComponent.navigateAddWorkspace()
                    }, dashboardComponent)
                }, workSpaceAndChannels = { modifier ->
                    SlackWorkspaceLayoutDesktop(
                        modifier,
                        onItemClick = { skChannel ->
                            onItemClick(skChannel)
                        },
                        onCreateChannelRequest = {
                            dashboardComponent.navigateRoot(RootComponent.Config.SearchCreateChannelUI)
                        },
                        dashboardComponent.recentChannelsComponent,
                        dashboardComponent.allChannelsComponent,
                        dashboardComponent
                    )
                }) { contentModifier ->
                    lastChannel?.let { slackChannel ->
                        ChatScreenUI(
                            modifier = contentModifier,
                            onBackClick = {
                                dashboardVM.isChatViewClosed.value = true
                                dashboardVM.selectedChatChannel.value = null
                            },
                            chatScreenComponent = chatScreenComponent,
                            slackChannel = slackChannel
                        )
                    } ?: run {
                        SlackCloneSurface(
                            color = LocalSlackCloneColor.current.uiBackground,
                            modifier = contentModifier
                        ) {
                            Children(stack = dashboardComponent.desktopStack, animation = stackAnimation(fade())) {
                                when (val child = it.instance) {
                                    is Dashboard.Child.DirectMessagesScreen -> DirectMessagesUI(
                                        onItemClick = onItemClick,
                                        child.component
                                    )

                                    is Dashboard.Child.MentionsScreen -> MentionsReactionsUI()
                                    is Dashboard.Child.SearchScreen -> SearchMessagesUI()
                                    is Dashboard.Child.UserProfileScreen -> UserProfileUI(child.component)

                                    is Dashboard.Child.HomeScreen -> throw RuntimeException("Not expecting to load home in desktop layout!")
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
internal fun SlackDualPaneLayoutView(
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
internal fun DashboardScaffold(
    needsOverlay: Boolean,
    scaffoldState: ScaffoldState,
    modifier: Modifier,
    appBarIconClick: () -> Unit,
    onItemClick: (DomainLayerChannels.SKChannel) -> Unit,
    dashboardComponent: DashboardComponent
) {
    Box(modifier) {
        Scaffold(
            backgroundColor = LocalSlackCloneColor.current.uiBackground,
            contentColor = LocalSlackCloneColor.current.textSecondary,
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
                    color = LocalSlackCloneColor.current.uiBackground,
                    modifier = Modifier.fillMaxSize()
                ) {
                    DashboardChildren(
                        Modifier,
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
internal fun DashboardChildren(
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
                    },
                    dashboardComponent.recentChannelsComponent,
                    dashboardComponent.allChannelsComponent
                )
            }

            is Dashboard.Child.DirectMessagesScreen -> DirectMessagesUI(onItemClick = onItemClick, child.component)
            is Dashboard.Child.MentionsScreen -> MentionsReactionsUI()
            is Dashboard.Child.SearchScreen -> SearchMessagesUI()
            is Dashboard.Child.UserProfileScreen -> {
                UserProfileUI(child.component)
            }
        }
    }
}

@Composable
internal fun FloatingDM(onClick: () -> Unit) {
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
internal fun OverlayDark(appBarIconClick: () -> Unit) {
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
internal fun DashboardBottomNavBar(dashboardComponent: DashboardComponent) {
    Column(Modifier.background(color = LocalSlackCloneColor.current.uiBackground)) {
        Divider(
            color = LocalSlackCloneColor.current.textPrimary.copy(alpha = 0.2f),
            thickness = 0.5.dp
        )
        BottomNavigation(backgroundColor = LocalSlackCloneColor.current.uiBackground) {
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
internal fun RowScope.BottomNavItem(
    screen: DashboardComponent.Config,
    navBackStackEntry: Dashboard.Child,
    dashboardComponent: DashboardComponent
) {
    BottomNavigationItem(
        selectedContentColor = LocalSlackCloneColor.current.bottomNavSelectedColor,
        unselectedContentColor = LocalSlackCloneColor.current.bottomNavUnSelectedColor,
        icon = { Icon(Icons.Default.Home, contentDescription = null, Modifier.size(24.dp)) },
        label = {
            Text(
                screen.name,
                maxLines = 1,
                style = SlackCloneTypography.overline
            )
        },
        selected = dashboardComponent.phoneStack.active == navBackStackEntry,
        onClick = {
            dashboardComponent.navigate(screen)
        }
    )
}
