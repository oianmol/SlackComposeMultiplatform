package dev.baseio.slackclone.uidashboard.compose.layouts

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.commonui.reusable.SlackImageBox
import dev.baseio.slackclone.commonui.reusable.SlackOnlineBox
import dev.baseio.slackclone.commonui.theme.SlackCloneColorProvider
import dev.baseio.slackclone.commonui.theme.SlackCloneSurface

import dev.baseio.slackclone.uidashboard.compose.*
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SlackSideBarLayoutDesktop(modifier: Modifier = Modifier, viewModel: SideNavVM) {
  val workspaces by viewModel.workspacesFlow.value.collectAsState(emptyList())
  val user by viewModel.currentLoggedInUser.collectAsState()

  Surface(modifier = modifier, color = SlackCloneColorProvider.colors.appBarColor) {
    var selected by remember { mutableStateOf(1) }
    Column(verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(4.dp))
        WorkSpacesDesktop(workspaces, viewModel)
        Spacer(Modifier.height(4.dp))
        SelectedSideBarIcon(Modifier.clickable {
          selected = 1
        }, Icons.Default.Home, isSelected = selected == 1)
        Spacer(Modifier.height(8.dp))
        SelectedSideBarIcon(Modifier.clickable {
          selected = 2
        }, Icons.Default.Email, selected == 2)
        Spacer(Modifier.height(8.dp))
        SelectedSideBarIcon(Modifier.clickable {
          selected = 3
        }, Icons.Default.Search, selected == 3)

        if (selected == 4) {
          AlertDialog(onDismissRequest = {
            selected = 1
          }, buttons = {
            MoreOptionsSideBarDesktop()
          })
        }

        SelectedSideBarIcon(
          Modifier.clickable {
            selected = 4
          }, Icons.Default.Add, false
        )
        Spacer(Modifier.height(8.dp))
      }
      SlackOnlineBox(
        user?.avatarUrl
          ?: "https://lh3.googleusercontent.com/a-/AFdZucqng-xqztAwJco6kqpNaehNMg6JbX4C5rYwv9VsNQ=s576-p-rw-no",
        parentModifier = Modifier.size(48.dp),
        imageModifier = Modifier.size(36.dp)
      )
    }
  }
}

@Composable
private fun MoreOptionsSideBarDesktop() {
  SlackCloneSurface(
    modifier = Modifier.shadow(4.dp),
    shape = RoundedCornerShape(4.dp)
  ) {
    Column(Modifier.padding(12.dp)) {
      Text("Sign in to another workspace", Modifier.padding(10.dp).clickable {

      })
      Text("Create a new workspace", Modifier.padding(10.dp).clickable {

      })
      Text("Find workspaces", Modifier.padding(10.dp).clickable {

      })
    }
  }
}

@Composable
private fun WorkSpacesDesktop(
  workspaces: List<DomainLayerWorkspaces.SKWorkspace>,
  viewModel: SideNavVM
) {
  LazyColumn {
    items(workspaces) { skWorkspace ->
      Column(Modifier.clickable {
        viewModel.select(skWorkspace)
      }) {
        WorkspaceDesktop(workspace = skWorkspace)
        Spacer(modifier = Modifier.padding(4.dp))
      }
    }
  }
}

@Composable
private fun WorkspaceDesktop(workspace: DomainLayerWorkspaces.SKWorkspace) {
  Box(
    Modifier.background(
      color = if (workspace.lastSelected) SlackCloneColorProvider.colors.textPrimary.copy(alpha = 0.2f) else Color.Transparent,
      shape = RoundedCornerShape(12.dp)
    )
  ) {
    Row(
      modifier = Modifier.align(Alignment.Center)
        .padding(4.dp), horizontalArrangement = Arrangement.Center
    ) {
      OrganizationLogo(
        Modifier
          .size(48.dp), Modifier
          .size(40.dp), workspace.picUrl, workspace.lastSelected
      )
    }
  }
}

@Composable
private fun SelectedSideBarIcon(modifier: Modifier = Modifier, icon: ImageVector, isSelected: Boolean) {
  Box(
    modifier.size(48.dp)
      .background(
        if (isSelected) SlackCloneColorProvider.colors.onUiBackground else Color.Transparent,
        shape = RoundedCornerShape(30)
      )
  ) {
    Icon(
      icon, contentDescription = null,
      modifier = Modifier.align(Alignment.Center).size(36.dp), tint = SlackCloneColorProvider.colors.appBarIconColor
    )
  }
}