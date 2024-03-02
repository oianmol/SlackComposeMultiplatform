package dev.baseio.slackclone.chatcore.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.common.extensions.calculateTimeAgo
import dev.baseio.slackclone.commonui.reusable.SlackListItem
import dev.baseio.slackclone.commonui.reusable.SlackOnlineBox
import dev.baseio.slackclone.commonui.theme.LocalSlackCloneColor
import dev.baseio.slackclone.commonui.theme.SlackCloneTypography
import dev.baseio.slackdomain.model.channel.DomainLayerChannels
import dev.baseio.slackdomain.model.message.DomainLayerMessages
import kotlinx.datetime.Clock

@Composable
internal fun SlackChannelItem(
    modifier: Modifier = Modifier.fillMaxWidth(),
    slackChannel: DomainLayerChannels.SKChannel,
    textColor: Color = LocalSlackCloneColor.current.textPrimary,
    onItemClick: (DomainLayerChannels.SKChannel) -> Unit
) {
    when (slackChannel) {
        is DomainLayerChannels.SKChannel.SkDMChannel -> {
            DirectMessageChannel(modifier, onItemClick, slackChannel, textColor)
        }

        is DomainLayerChannels.SKChannel.SkGroupChannel -> {
            GroupChannelItem(slackChannel, onItemClick, textColor)
        }
    }
}

@Composable
internal fun GroupChannelItem(
    slackChannel: DomainLayerChannels.SKChannel.SkGroupChannel,
    onItemClick: (DomainLayerChannels.SKChannel) -> Unit,
    textColor: Color
) {
    SlackListItem(
        icon = Icons.Default.Lock,
        title = slackChannel.name,
        textColor = textColor,
        onItemClick = {
            onItemClick(slackChannel)
        })
}

@Composable
internal fun DirectMessageChannel(
    modifier: Modifier = Modifier.fillMaxWidth(),
    onItemClick: (DomainLayerChannels.SKChannel) -> Unit,
    slackChannel: DomainLayerChannels.SKChannel.SkDMChannel,
    textColor: Color
) {
    Row(
        modifier = modifier.padding(8.dp).clickable {
            onItemClick(slackChannel)
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        SlackOnlineBox(imageUrl = slackChannel.pictureUrl ?: "")
        ChannelText(slackChannel, textColor)
    }
}

@Composable
internal fun DMLastMessageItem(
    onItemClick: (DomainLayerChannels.SKChannel) -> Unit,
    slackChannel: DomainLayerChannels.SKChannel,
    slackMessage: DomainLayerMessages.SKMessage
) {
    Row(
        modifier = Modifier.padding(horizontal = 4.dp).fillMaxWidth().clickable {
            onItemClick(slackChannel)
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        SlackListItem(modifier = Modifier, icon = {
            when (slackChannel) {
                is DomainLayerChannels.SKChannel.SkGroupChannel -> {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = LocalSlackCloneColor.current.textPrimary.copy(alpha = 0.4f),
                        modifier = Modifier.size(28.dp).padding(4.dp)
                    )
                }

                is DomainLayerChannels.SKChannel.SkDMChannel -> {
                    SlackOnlineBox(
                        imageUrl = slackChannel.pictureUrl ?: ""
                    )
                }
            }
        }, center = {
            Column(it.weight(1f).padding(4.dp)) {
                ChannelText(slackChannel, LocalSlackCloneColor.current.textPrimary)
                ChannelMessage(slackMessage, LocalSlackCloneColor.current.textSecondary)
            }
        }, trailingItem = {
            RelativeTime(slackMessage.createdDate)
        }, onItemClick = {
            onItemClick(slackChannel)
        })
    }
}

@Composable
internal fun ChannelMessage(slackMessage: DomainLayerMessages.SKMessage, textSecondary: Color) {
    Text(
        text = slackMessage.decodedMessage ?: "Waiting for message.",
        style = SlackCloneTypography.caption.copy(
            color = textSecondary.copy(
                alpha = 0.8f
            )
        ),
        modifier = Modifier.padding(4.dp),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
internal fun RelativeTime(createdDate: Long) {
    Text(
        calculateTimeAgo(Clock.System.now().toEpochMilliseconds(), createdDate),
        style = SlackCloneTypography.caption.copy(
            color = LocalSlackCloneColor.current.textSecondary
        ),
        modifier = Modifier.padding(4.dp)
    )
}

@Composable
internal fun ChannelText(slackChannel: DomainLayerChannels.SKChannel, textColor: Color) {
    Text(
        text = "${slackChannel.channelName}",
        style = SlackCloneTypography.caption.copy(
            color = textColor.copy(
                alpha = 0.8f
            )
        ),
        modifier = Modifier.padding(4.dp),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}
