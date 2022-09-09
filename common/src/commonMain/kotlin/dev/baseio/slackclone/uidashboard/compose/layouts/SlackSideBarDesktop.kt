package dev.baseio.slackclone.uidashboard.compose.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.commonui.reusable.SlackImageBox
import dev.baseio.slackclone.commonui.reusable.SlackOnlineBox
import dev.baseio.slackclone.commonui.theme.SlackCloneColorProvider

@Composable
fun SlackSideBarLayoutDesktop(modifier: Modifier = Modifier) {
  Surface(modifier = modifier, color = SlackCloneColorProvider.colors.appBarColor) {
    var selected by remember { mutableStateOf(1) }
    Column(verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.CenterHorizontally) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(12.dp))
        SlackImageBox(
          Modifier.padding(12.dp),
          "https://avatars.slack-edge.com/2018-07-20/401750958992_1b07bb3c946bc863bfc6_88.png"
        )
        Spacer(Modifier.height(12.dp))
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
        Spacer(Modifier.height(8.dp))
      }
      SlackOnlineBox(
        "https://lh3.googleusercontent.com/a-/AFdZucqng-xqztAwJco6kqpNaehNMg6JbX4C5rYwv9VsNQ=s576-p-rw-no",
        parentModifier = Modifier.size(48.dp),
        imageModifier = Modifier.size(36.dp)
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