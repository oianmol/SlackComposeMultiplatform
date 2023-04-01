package dev.baseio.slackclone.dashboard.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.channels.SlackChannelComponent
import dev.baseio.slackclone.channels.views.SlackAllChannels
import dev.baseio.slackclone.channels.views.SlackRecentChannels
import dev.baseio.slackclone.commonui.material.SlackSurfaceAppBar
import dev.baseio.slackclone.commonui.reusable.SlackImageBox
import dev.baseio.slackclone.commonui.reusable.SlackListItem
import dev.baseio.slackclone.commonui.theme.LocalSlackCloneColor
import dev.baseio.slackclone.commonui.theme.SlackCloneSurface
import dev.baseio.slackclone.commonui.theme.SlackCloneTypography
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces

@Composable
internal fun HomeScreenUI(
    homeScreenComponent: HomeScreenComponent,
    appBarIconClick: () -> Unit,
    onItemClick: (DomainLayerChannels.SKChannel) -> Unit = {},
    onCreateChannelRequest: () -> Unit = {},
    recentChannelsComponent: SlackChannelComponent,
    allChannelsComponent: SlackChannelComponent
) {
    val selectedWorkspace by homeScreenComponent.lastSelectedWorkspace.value.collectAsState(null)
    SlackCloneSurface(
        color = LocalSlackCloneColor.current.uiBackground,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            SlackWorkspaceTopAppBar(appBarIconClick, selectedWorkspace)
            Column(Modifier.verticalScroll(rememberScrollState())) {
                JumpToText()
                ThreadsTile()
                SlackListDivider()
                SlackRecentChannels({
                    onItemClick(it)
                }, onClickAdd = {
                    onCreateChannelRequest()
                }, recentChannelsComponent)
                SlackAllChannels({
                    onItemClick(it)
                }, onClickAdd = {
                    onCreateChannelRequest()
                }, allChannelsComponent)
            }
        }
    }
}

@Composable
internal fun ThreadsTile() {
    SlackListItem(icon = Icons.Default.MailOutline, title = "Threads")
}

@Composable
internal fun JumpToText() {
    Box(
        Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(12.dp)
    ) {
        RoundedCornerBoxDecoration {
            Text(
                text = "Jump to...",
                style = SlackCloneTypography.subtitle2.copy(color = LocalSlackCloneColor.current.textPrimary),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
internal fun SlackWorkspaceTopAppBar(
    appBarIconClick: () -> Unit,
    selectedWorkspace: DomainLayerWorkspaces.SKWorkspace?
) {
    SlackSurfaceAppBar(
        title = {
            Text(text = selectedWorkspace?.name ?: "NA", style = SlackCloneTypography.h5.copy(color = Color.White))
        },
        navigationIcon = {
            WorkspaceImageButton(appBarIconClick, selectedWorkspace)
        },
        backgroundColor = LocalSlackCloneColor.current.appBarColor
    )
}

@Composable
internal fun WorkspaceImageButton(appBarIconClick: () -> Unit, selectedWorkspace: DomainLayerWorkspaces.SKWorkspace?) {
    IconButton(onClick = {
        appBarIconClick()
    }) {
        SlackImageBox(
            Modifier.size(38.dp),
            selectedWorkspace?.picUrl ?: "https://avatars.slack-edge.com/2018-07-20/401750958992_1b07bb3c946bc863bfc6_88.png"
        )
    }
}
