package dev.baseio.slackclone.dashboard.compose.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import dev.baseio.slackclone.commonui.reusable.SlackOnlineBox
import dev.baseio.slackclone.commonui.theme.LocalSlackCloneColor
import dev.baseio.slackclone.dashboard.compose.OrganizationLogo
import dev.baseio.slackclone.dashboard.vm.DashboardComponent
import dev.baseio.slackclone.dashboard.vm.SideNavComponent
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces

@Composable
internal fun SlackSideBarLayoutDesktop(
    modifier: Modifier = Modifier,
    sideNavComponent: SideNavComponent,
    openDM: () -> Unit,
    mentionsScreen: () -> Unit,
    searchScreen: () -> Unit,
    userProfile: () -> Unit,
    qrCode: () -> Unit,
    addWorkspace:()->Unit,
    dashboardComponent: DashboardComponent
) {
    val workspaces by sideNavComponent.viewModel.flow().collectAsState(emptyList())
    val user by sideNavComponent.viewModel.currentLoggedInUser.collectAsState()
    val state by dashboardComponent.desktopStack.subscribeAsState()

    Surface(modifier = modifier, color = LocalSlackCloneColor.current.appBarColor) {
        Column(verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(4.dp))
                WorkSpacesDesktop(workspaces, sideNavComponent)
                SelectedSideBarIcon(
                    modifier = Modifier.padding(vertical = 8.dp).clickable {
                        openDM()
                    },
                    icon = Icons.Default.Email,
                    isSelected = state.active.configuration == DashboardComponent.Config.DirectMessages
                ) // dm
                SelectedSideBarIcon(
                    modifier = Modifier.padding(vertical = 8.dp).clickable {
                        mentionsScreen()
                    },
                    icon = Icons.Default.Notifications,
                    isSelected = state.active.configuration == DashboardComponent.Config.MentionsConfig
                ) // mention
                SelectedSideBarIcon(
                    modifier = Modifier.padding(vertical = 8.dp).clickable {
                        searchScreen()
                    },
                    icon = Icons.Default.Search,
                    isSelected = state.active.configuration == DashboardComponent.Config.Search
                ) // search
                SelectedSideBarIcon(
                    modifier = Modifier.padding(vertical = 8.dp).clickable {
                        userProfile()
                    },
                    icon = Icons.Default.AccountCircle,
                    isSelected = state.active.configuration == DashboardComponent.Config.Profile
                ) // You
                SelectedSideBarIcon(
                    modifier = Modifier.padding(vertical = 8.dp).clickable {
                        qrCode()
                    },
                    icon = Icons.Default.Build,
                    isSelected = false
                ) // QRCode
                SelectedSideBarIcon(
                    modifier = Modifier.padding(vertical = 8.dp).clickable {
                        addWorkspace()
                    },
                    icon = Icons.Default.AddCircle,
                    isSelected = false
                ) //
                Spacer(Modifier.height(4.dp))
            }
            SlackOnlineBox(
                user?.avatarUrl
                    ?: "https://lh3.googleusercontent.com/a-/AFdZucqng-xqztAwJco6kqpNaehNMg6JbX4C5rYwv9VsNQ=s576-p-rw-no",
                parentModifier = Modifier.size(48.dp).clickable {
                    userProfile()
                },
                imageModifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
internal fun WorkSpacesDesktop(
    workspaces: List<DomainLayerWorkspaces.SKWorkspace>,
    component: SideNavComponent
) {
    LazyColumn {
        itemsIndexed(workspaces) { index, skWorkspace ->
            Column(
                Modifier.clickable {
                    component.viewModel.select(skWorkspace)
                }
            ) {
                WorkspaceDesktop(workspace = skWorkspace, index == 0)
                Spacer(modifier = Modifier.padding(4.dp))
            }
        }
    }
}

@Composable
internal fun WorkspaceDesktop(workspace: DomainLayerWorkspaces.SKWorkspace, lastSelected: Boolean) {
    Box(
        Modifier.background(
            color = if (lastSelected) LocalSlackCloneColor.current.textPrimary.copy(alpha = 0.2f) else Color.Transparent,
            shape = RoundedCornerShape(12.dp)
        )
    ) {
        Row(
            modifier = Modifier.align(Alignment.Center)
                .padding(4.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            OrganizationLogo(
                Modifier
                    .size(48.dp),
                Modifier
                    .size(40.dp),
                workspace.picUrl,
                lastSelected
            )
        }
    }
}

@Composable
internal fun SelectedSideBarIcon(modifier: Modifier = Modifier, icon: ImageVector, isSelected: Boolean) {
    Box(
        modifier.size(48.dp)
            .background(
                if (isSelected) LocalSlackCloneColor.current.onUiBackground else Color.Transparent,
                shape = RoundedCornerShape(30)
            )
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.align(Alignment.Center).size(36.dp),
            tint = LocalSlackCloneColor.current.appBarIconColor
        )
    }
}
