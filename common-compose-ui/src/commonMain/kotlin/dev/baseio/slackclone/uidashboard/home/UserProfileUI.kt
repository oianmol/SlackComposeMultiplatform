package dev.baseio.slackclone.uidashboard.home

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.commonui.material.SlackSurfaceAppBar
import dev.baseio.slackclone.commonui.reusable.SlackImageBox
import dev.baseio.slackclone.commonui.reusable.SlackListItem
import dev.baseio.slackclone.commonui.theme.LocalSlackCloneColor
import dev.baseio.slackclone.commonui.theme.SlackCloneSurface
import dev.baseio.slackclone.commonui.theme.SlackCloneTypography
import dev.baseio.slackdomain.model.users.DomainLayerUsers

@Composable
internal fun UserProfileUI(component: UserProfileComponent, profileVM: UserProfileVM = component.viewModel) {
    val user by profileVM.currentLoggedInUser.collectAsState()

    SlackCloneSurface(
        color = LocalSlackCloneColor.current.uiBackground,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            SearchTopAppBar()
            UserHeader(user)
            Box(Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                StatusBox()
            }
            SlackListItem(icon = Icons.Default.Notifications, title = "Pause Notifications")
            SlackListItem(icon = Icons.Default.Person, title = "Set Away")
            Divider(color = LocalSlackCloneColor.current.lineColor, thickness = 0.5.dp)
            SlackListItem(icon = Icons.Default.FavoriteBorder, title = "Saved Items")
            SlackListItem(icon = Icons.Default.Person, title = "View Profile")
            SlackListItem(icon = Icons.Default.Notifications, title = "Notifications")
            SlackListItem(icon = Icons.Default.ExitToApp, title = "Logout", onItemClick = {
                profileVM.logout()
                component.navigateOnboardingRoot()
            })
        }
    }
}

@Composable
internal fun SearchTopAppBar() {
    SlackSurfaceAppBar(
        title = {
            Text(
                text = "You",
                style = SlackCloneTypography.h5.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
        },
        backgroundColor = LocalSlackCloneColor.current.appBarColor
    )
}

@Composable
internal fun SlackListItemTrailingView(
    icon: ImageVector,
    title: String,
    trailingView: @Composable () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .padding(12.dp)
            .clickable { }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = LocalSlackCloneColor.current.textPrimary.copy(alpha = 0.4f),
            modifier = Modifier
                .size(24.dp)
                .padding(4.dp)
        )
        Text(
            text = title,
            style = SlackCloneTypography.subtitle1.copy(
                color = LocalSlackCloneColor.current.textPrimary.copy(
                    alpha = 0.8f
                )
            ),
            modifier = Modifier
                .weight(1f)
                .padding(4.dp)
        )
        trailingView()
    }
}

@Composable
internal fun UserHeader(user: DomainLayerUsers.SKUser?) {
    Row(Modifier.padding(12.dp)) {
        SlackImageBox(
            Modifier.size(72.dp),
            user?.avatarUrl
                ?: "https://lh3.googleusercontent.com/a-/AFdZucqng-xqztAwJco6kqpNaehNMg6JbX4C5rYwv9VsNQ=s576-p-rw-no"
        )
        Column(Modifier.padding(start = 8.dp)) {
            Text(text = user?.name ?: "Anmol Verma", style = SlackCloneTypography.h6.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.padding(top = 4.dp))
            Text(
                text = "Active",
                style = SlackCloneTypography.subtitle1.copy(
                    fontWeight = FontWeight.Bold,
                    color = LocalSlackCloneColor.current.textPrimary.copy(alpha = 0.4f)
                )
            )
        }
    }
}

@Composable
internal fun StatusBox() {
    RoundedCornerBoxDecoration {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🌴", modifier = Modifier.padding(2.dp), style = SlackCloneTypography.subtitle1)
            Text(
                text = "Out on a vacation",
                style = SlackCloneTypography.body1.copy(
                    fontWeight = FontWeight.Normal,
                    color = LocalSlackCloneColor.current.textPrimary
                ),
                modifier = Modifier
                    .padding(2.dp)
                    .weight(1f),
                textAlign = TextAlign.Start
            )
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = null,
                tint = LocalSlackCloneColor.current.textPrimary
            )
        }
    }
}

@Composable
internal fun RoundedCornerBoxDecoration(content: @Composable () -> Unit) {
    Box(
        Modifier
            .border(
                width = 1.dp,
                color = LocalSlackCloneColor.current.lineColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        content()
    }
}
