package dev.baseio.slackclone.uidashboard.compose

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
import dev.baseio.slackclone.commonui.material.SlackSurfaceAppBar
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.commonui.reusable.SlackImageBox
import dev.baseio.slackclone.commonui.theme.*
import dev.baseio.slackclone.commonui.reusable.SlackListItem
import dev.baseio.slackclone.uidashboard.vm.SideNavComponent

import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces

@Composable
fun SideNavigation(
  modifier: Modifier,
  viewModel: SideNavComponent,
  onClose: () -> Unit
) {
  val workspaces by viewModel.workspacesFlow.value.collectAsState(emptyList())
  SlackCloneSurface(color = SlackCloneColorProvider.colors.uiBackground, modifier = modifier.fillMaxSize()) {
    Column(
      modifier = Modifier
        .fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween
    ) {
      LazyColumn {
        item {
          WorkspacesBar()
        }
        itemsIndexed(workspaces) { index, skWorkspace ->
          Column(Modifier.clickable {
            viewModel.select(skWorkspace)
            onClose()
          }) {
            Workspace(workspace = skWorkspace, index == 0)
            Spacer(modifier = Modifier.padding(8.dp))
          }
        }
      }
      Spacer(modifier = Modifier.padding(8.dp))
      SideNavFooter(logout = {
        viewModel.logout()
        TODO("impl go back to onboarding")
       /* composeNavigator.navigateRoute(SlackScreens.OnboardingRoute, removeRoute = { it, remove ->
          remove() // remove all routes!
        })*/
      })
    }

  }
}

@Composable
private fun SideNavFooter(logout: () -> Unit) {
  Column(modifier = Modifier) {
    Divider(color = SlackCloneColorProvider.colors.lineColor)
    SlackListItem(icon = Icons.Filled.AddCircle, title = "Add Workspace")
    SlackListItem(icon = Icons.Filled.Settings, title = "Preferences")
    SlackListItem(icon = Icons.Filled.CheckCircle, title = "Help")
    SlackListItem(icon = Icons.Filled.ExitToApp, title = "Logout", onItemClick = {
      logout()
    })
  }
}

@Composable
private fun Workspace(workspace: DomainLayerWorkspaces.SKWorkspace, lastSelected: Boolean) {
  Box(
    Modifier.background(
      color = if (lastSelected) SlackCloneColorProvider.colors.textPrimary.copy(alpha = 0.2f) else Color.Transparent,
      shape = RoundedCornerShape(12.dp)
    )
  ) {
    Row(
      modifier = Modifier
        .padding(8.dp)
        .fillMaxWidth(), horizontalArrangement = Arrangement.Center
    ) {
      OrganizationLogo(
        picUrl = workspace.picUrl, lastSelected = lastSelected
      )
      Box(Modifier.weight(1f)) {
        OrganizationDetails(workspace)
      }
      Icon(
        imageVector = Icons.Filled.MoreVert,
        contentDescription = null,
        tint = SlackCloneColorProvider.colors.textPrimary
      )
    }
  }
}

@Composable
fun OrganizationDetails(workspace: DomainLayerWorkspaces.SKWorkspace) {
  Column(
    modifier = Modifier
      .padding(start = 8.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.Start
  ) {
    Text(
      text = workspace.name,
      style = SlackCloneTypography.h6.copy(
        color = SlackCloneColorProvider.colors.textPrimary,
        fontWeight = FontWeight.SemiBold
      )
    )
    Text(
      workspace.domain,
      style = SlackCloneTypography.subtitle1.copy(
        fontWeight = FontWeight.Normal,
        color = SlackCloneColorProvider.colors.textPrimary.copy(alpha = 0.4f)
      ),
    )
  }
}

@Composable
fun OrganizationLogo(
  modifierParent: Modifier = Modifier.size(68.dp),
  modifierChild: Modifier = Modifier.size(64.dp),
  picUrl: String?,
  lastSelected: Boolean
) {
  Box(
    modifierParent
      .border(
        width = 3.dp,
        color = if (lastSelected) SlackCloneColorProvider.colors.textPrimary else Color.Transparent,
        shape = RoundedCornerShape(12.dp)
      )
  ) {
    SlackImageBox(
      modifierChild.align(Alignment.Center),
      picUrl ?: "https://avatars.slack-edge.com/2018-07-20/401750958992_1b07bb3c946bc863bfc6_88.png"
    )
  }
}

@Composable
fun WorkspacesBar() {
  SlackSurfaceAppBar(
    backgroundColor = SlackCloneColorProvider.colors.appBarColor,
    elevation = 0.dp,
  ) {
    Text(
      text = "Workspaces",
      style = SlackCloneTypography.h5.copy(
        color = SlackCloneColorProvider.colors.appBarTextTitleColor,
        fontWeight = FontWeight.Bold
      ),
      modifier = Modifier.padding(start = 8.dp)
    )
  }
}