package dev.baseio.slackclone.uidashboard.compose.layouts

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.commonui.reusable.SlackOnlineBox
import dev.baseio.slackclone.commonui.theme.SlackCloneColorProvider
import dev.baseio.slackclone.commonui.theme.SlackCloneSurface
import dev.baseio.slackclone.navigation.SlackComposeNavigator
import dev.baseio.slackclone.navigation.SlackScreens

import dev.baseio.slackclone.uidashboard.compose.*
import dev.baseio.slackclone.uidashboard.vm.SideNavVM
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces

@Composable
fun SlackSideBarLayoutDesktop(
  modifier: Modifier = Modifier,
  viewModel: SideNavVM,
  openDM: () -> Unit,
  mentionsScreen: () -> Unit,
  searchScreen: () -> Unit,
  userProfile: () -> Unit,
  navigator: SlackComposeNavigator,
) {
  val workspaces by viewModel.workspacesFlow.value.collectAsState(emptyList())
  val user by viewModel.currentLoggedInUser.collectAsState()

  Surface(modifier = modifier, color = SlackCloneColorProvider.colors.appBarColor) {
    Column(verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(4.dp))
        WorkSpacesDesktop(workspaces, viewModel)
        SelectedSideBarIcon(
          modifier = Modifier.padding(vertical = 8.dp).clickable {
            openDM()
          },
          icon = Icons.Default.Email,
          isSelected = navigator.lastScreen == SlackScreens.DMs
        )//dm
        SelectedSideBarIcon(
          modifier = Modifier.padding(vertical = 8.dp).clickable {
            mentionsScreen()
          },
          icon = Icons.Default.Notifications,
          isSelected = navigator.lastScreen == SlackScreens.Mentions
        )//mention
        SelectedSideBarIcon(
          modifier = Modifier.padding(vertical = 8.dp).clickable {
            searchScreen()
          },
          icon = Icons.Default.Search,
          isSelected = navigator.lastScreen == SlackScreens.Search
        )//search
        SelectedSideBarIcon(
          modifier = Modifier.padding(vertical = 8.dp).clickable {
            userProfile()
          },
          icon = Icons.Default.AccountCircle,
          isSelected = navigator.lastScreen == SlackScreens.You
        )//You
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
private fun MoreOptionsSideBarDesktop(
  signIntoWorkspace: () -> Unit,
  createNewWorkspace: () -> Unit,
  findWorkspaces: () -> Unit
) {
  SlackCloneSurface(
    modifier = Modifier.shadow(4.dp),
    shape = RoundedCornerShape(4.dp)
  ) {
    Column(Modifier.padding(12.dp)) {
      Text("Sign in to another workspace", Modifier.padding(10.dp).clickable {
        signIntoWorkspace()
      })
      Text("Create a new workspace", Modifier.padding(10.dp).clickable {
        createNewWorkspace()
      })
      Text("Find workspaces", Modifier.padding(10.dp).clickable {
        findWorkspaces()
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
    itemsIndexed(workspaces) { index, skWorkspace ->
      Column(Modifier.clickable {
        viewModel.select(skWorkspace)
      }) {
        WorkspaceDesktop(workspace = skWorkspace, index == 0)
        Spacer(modifier = Modifier.padding(4.dp))
      }
    }
  }
}

@Composable
private fun WorkspaceDesktop(workspace: DomainLayerWorkspaces.SKWorkspace, lastSelected: Boolean) {
  Box(
    Modifier.background(
      color = if (lastSelected) SlackCloneColorProvider.colors.textPrimary.copy(alpha = 0.2f) else Color.Transparent,
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
          .size(40.dp), workspace.picUrl, lastSelected
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
      icon,
      contentDescription = null,
      modifier = Modifier.align(Alignment.Center).size(36.dp),
      tint = SlackCloneColorProvider.colors.appBarIconColor
    )
  }
}