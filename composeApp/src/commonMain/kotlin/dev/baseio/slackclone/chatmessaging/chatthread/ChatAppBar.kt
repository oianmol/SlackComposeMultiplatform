package dev.baseio.slackclone.chatmessaging.chatthread

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.chatcore.views.SlackChannelItem
import dev.baseio.slackclone.commonui.material.SlackSurfaceAppBar
import dev.baseio.slackclone.commonui.theme.LocalSlackCloneColor
import dev.baseio.slackdomain.model.channel.DomainLayerChannels

@Composable
internal fun ChatAppBar(
    onBackClick: () -> Unit,
    channel: DomainLayerChannels.SKChannel,
    showChannelDetailsRequested: () -> Unit
) {
    SlackSurfaceAppBar(backgroundColor = LocalSlackCloneColor.current.appBarColor) {
        IconButton(onClick = { onBackClick() }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                tint = LocalSlackCloneColor.current.appBarIconColor,
                modifier = Modifier.size(24.dp)
            )
        }
        Column(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            SlackChannelItem(
                modifier = Modifier,
                slackChannel = channel,
                textColor = LocalSlackCloneColor.current.appBarTextTitleColor
            ) {
                showChannelDetailsRequested()
            }
        }
        IconButton(onClick = { }) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = null,
                tint = LocalSlackCloneColor.current.appBarIconColor,
                modifier = Modifier
                    .size(24.dp)
            )
        }
    }
}