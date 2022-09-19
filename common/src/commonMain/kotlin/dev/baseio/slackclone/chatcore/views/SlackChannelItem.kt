package dev.baseio.slackclone.chatcore.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.chatcore.data.UiLayerChannels
import dev.baseio.slackclone.common.extensions.calculateTimeAgoByTimeGranularity
import dev.baseio.slackclone.commonui.reusable.SlackListItem
import dev.baseio.slackclone.commonui.reusable.SlackOnlineBox
import dev.baseio.slackclone.commonui.theme.SlackCloneColorProvider
import dev.baseio.slackclone.commonui.theme.SlackCloneTypography
import dev.baseio.slackdomain.model.message.DomainLayerMessages
import kotlinx.datetime.Clock

@Composable
fun SlackChannelItem(
  slackChannel: UiLayerChannels.SKChannel,
  textColor: Color = SlackCloneColorProvider.colors.textPrimary,
  onItemClick: (UiLayerChannels.SKChannel) -> Unit
) {
  when (slackChannel.isOneToOne) {
    true -> {
      DirectMessageChannel(onItemClick, slackChannel, textColor)
    }

    else -> {
      GroupChannelItem(slackChannel, onItemClick)
    }
  }
}

@Composable
private fun GroupChannelItem(
  slackChannel: UiLayerChannels.SKChannel,
  onItemClick: (UiLayerChannels.SKChannel) -> Unit
) {
  SlackListItem(
    icon = if (slackChannel.isPrivate == true) Icons.Default.Lock else Icons.Default.MailOutline,
    title = "${slackChannel.name}",
    onItemClick = {
      onItemClick(slackChannel)
    }
  )
}

@Composable
private fun DirectMessageChannel(
  onItemClick: (UiLayerChannels.SKChannel) -> Unit,
  slackChannel: UiLayerChannels.SKChannel,
  textColor: Color
) {
  Row(
    modifier = Modifier
      .padding(8.dp)
      .fillMaxWidth()
      .clickable {
        onItemClick(slackChannel)
      }, verticalAlignment = Alignment.CenterVertically
  ) {
    SlackOnlineBox(imageUrl = slackChannel.pictureUrl ?: "")
    ChannelText(slackChannel, textColor)
  }
}

@Composable
fun DMLastMessageItem(
  onItemClick: (UiLayerChannels.SKChannel) -> Unit,
  slackChannel: UiLayerChannels.SKChannel,
  slackMessage: DomainLayerMessages.SKMessage,
) {
  Row(
    modifier = Modifier
      .padding(4.dp)
      .fillMaxWidth()
      .clickable {
        onItemClick(slackChannel)
      }, verticalAlignment = Alignment.CenterVertically
  ) {
    SlackListItem(modifier = Modifier, icon = {
      if (slackChannel.isOneToOne == false) {
        Box(Modifier.size(24.dp)) {
          Text(text = "#", style = textStyleFieldSecondary(), modifier = Modifier.align(Alignment.Center))
        }
      } else {
        SlackOnlineBox(
          imageUrl = slackChannel.pictureUrl ?: "",
          parentModifier = Modifier.size(24.dp),
          imageModifier = Modifier.size(20.dp),
          onlineIndicator = Modifier.size(10.dp),
          onlineIndicatorParent = Modifier.size(12.dp)
        )
      }

    }, center = {
      Column(it.padding(4.dp)) {
        ChannelText(slackChannel, SlackCloneColorProvider.colors.textPrimary)
        ChannelMessage(slackMessage, SlackCloneColorProvider.colors.textSecondary)
      }
    }, trailingItem = {
      RelativeTime(slackMessage.createdDate)
    }, onItemClick = {
      onItemClick(slackChannel)
    })

  }
}

@Composable
private fun textStyleFieldSecondary() = SlackCloneTypography.subtitle2.copy(
  color = SlackCloneColorProvider.colors.textSecondary,
  fontWeight = FontWeight.Normal,
  textAlign = TextAlign.Start
)

@Composable
private fun ChannelMessage(slackMessage: DomainLayerMessages.SKMessage, textSecondary: Color) {
  Text(
    text = slackMessage.message,
    style = SlackCloneTypography.caption.copy(
      color = textSecondary.copy(
        alpha = 0.8f
      ),
    ), modifier = Modifier
      .padding(4.dp),
    maxLines = 2,
    overflow = TextOverflow.Ellipsis
  )
}

@Composable
fun RelativeTime(createdDate: Long) {
  Text(
    calculateTimeAgoByTimeGranularity
      (Clock.System.now().toEpochMilliseconds(), createdDate),
    style = SlackCloneTypography.caption.copy(
      color = SlackCloneColorProvider.colors.textSecondary
    ), modifier = Modifier.padding(4.dp)
  )
}

@Composable
private fun ChannelText(
  slackChannel: UiLayerChannels.SKChannel,
  textColor: Color
) {
  Text(
    text = "${slackChannel.name}",
    style = SlackCloneTypography.subtitle2.copy(
      color = textColor.copy(
        alpha = 0.8f
      )
    ), modifier = Modifier
      .padding(4.dp), maxLines = 1,
    overflow = TextOverflow.Ellipsis
  )
}