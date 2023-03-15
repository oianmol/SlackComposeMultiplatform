package dev.baseio.slackclone.dashboard.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.commonui.material.SlackSurfaceAppBar
import dev.baseio.slackclone.commonui.reusable.SlackImageBox
import dev.baseio.slackclone.commonui.reusable.SlackListItem
import dev.baseio.slackclone.commonui.theme.*
import dev.baseio.slackclone.dashboard.vm.SideNavComponent
import dev.baseio.slackclone.qrscanner.QrScannerMode
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces

@Composable
internal fun SideNavigation(
    modifier: Modifier,
    sideNavComponent: SideNavComponent,
    onClose: () -> Unit,
    navigateOnboardingClearRoutes: () -> Unit,
    navigateQrScanner: (QrScannerMode) -> Unit,
    navigateAddWorkspace: () -> Unit
) {
    val workspaces by sideNavComponent.viewModel.flow().collectAsState(emptyList())
    SlackCloneSurface(
        color = LocalSlackCloneColor.current.uiBackground,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            LazyColumn {
                item {
                    WorkspacesBar()
                }
                itemsIndexed(workspaces) { _, skWorkspace ->
                    Column(
                        Modifier.clickable {
                            sideNavComponent.viewModel.select(skWorkspace)
                            onClose()
                        }
                    ) {
                        Workspace(workspace = skWorkspace, sideNavComponent.viewModel.isSelectedWorkspace(skWorkspace))
                        Spacer(modifier = Modifier.padding(8.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.padding(8.dp))
            SideNavFooter(logout = {
                sideNavComponent.viewModel.logout()
                navigateOnboardingClearRoutes()
            }, openQrScanner = {
                navigateQrScanner(QrScannerMode.QR_DISPLAY)
            }, addWorkspace = {
                navigateAddWorkspace()
            })
        }
    }
}

@Composable
internal fun SideNavFooter(
    logout: () -> Unit,
    openQrScanner: () -> Unit,
    addWorkspace: () -> Unit
) {
    Column(modifier = Modifier) {
        Divider(color = LocalSlackCloneColor.current.lineColor)
        SlackListItem(
            icon = Icons.Filled.AddCircle,
            title = "Authorize other device(s)",
            subtitle = "Use QR Camera",
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Start,
            onItemClick = {
                openQrScanner()
            })
        SlackListItem(icon = Icons.Filled.Add, title = "Add Workspace", onItemClick = {
            addWorkspace()
        })
        SlackListItem(icon = Icons.Filled.Settings, title = "Preferences")
        SlackListItem(icon = Icons.Filled.CheckCircle, title = "Help")
        SlackListItem(icon = Icons.Filled.ExitToApp, title = "Logout", onItemClick = {
            logout()
        })
    }
}

@Composable
internal fun Workspace(workspace: DomainLayerWorkspaces.SKWorkspace, lastSelected: Boolean) {
    Box(
        Modifier.background(
            color = if (lastSelected) LocalSlackCloneColor.current.textPrimary.copy(alpha = 0.2f) else Color.Transparent,
            shape = RoundedCornerShape(12.dp)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            OrganizationLogo(
                picUrl = workspace.picUrl,
                lastSelected = lastSelected
            )
            Box(Modifier.weight(1f)) {
                OrganizationDetails(workspace)
            }
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = null,
                tint = LocalSlackCloneColor.current.textPrimary
            )
        }
    }
}

@Composable
internal fun OrganizationDetails(workspace: DomainLayerWorkspaces.SKWorkspace) {
    Column(
        modifier = Modifier
            .padding(start = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = workspace.name,
            style = SlackCloneTypography.h6.copy(
                color = LocalSlackCloneColor.current.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
        )
        Text(
            workspace.domain,
            style = SlackCloneTypography.subtitle1.copy(
                fontWeight = FontWeight.Normal,
                color = LocalSlackCloneColor.current.textPrimary.copy(alpha = 0.4f)
            )
        )
    }
}

@Composable
internal fun OrganizationLogo(
    modifierParent: Modifier = Modifier.size(68.dp),
    modifierChild: Modifier = Modifier.size(64.dp),
    picUrl: String?,
    lastSelected: Boolean
) {
    Box(
        modifierParent
            .border(
                width = 3.dp,
                color = if (lastSelected) LocalSlackCloneColor.current.textPrimary else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        SlackImageBox(
            modifierChild.align(Alignment.Center),
            picUrl
                ?: "https://avatars.slack-edge.com/2018-07-20/401750958992_1b07bb3c946bc863bfc6_88.png"
        )
    }
}

@Composable
internal fun WorkspacesBar() {
    SlackSurfaceAppBar(
        backgroundColor = LocalSlackCloneColor.current.appBarColor,
        elevation = 0.dp
    ) {
        Text(
            text = "Workspaces",
            style = SlackCloneTypography.h5.copy(
                color = LocalSlackCloneColor.current.appBarTextTitleColor,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
