package dev.baseio.slackclone.channels.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.IconToggleButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.chatcore.views.SlackChannelItem
import dev.baseio.slackclone.commonui.theme.LocalSlackCloneColor
import dev.baseio.slackclone.commonui.theme.SlackCloneTypography
import dev.baseio.slackdomain.model.channel.DomainLayerChannels

@Composable
internal fun SKExpandCollapseColumn(
    expandCollapseModel: ExpandCollapseModel,
    onItemClick: (DomainLayerChannels.SKChannel) -> Unit = {},
    onExpandCollapse: (isChecked: Boolean) -> Unit,
    channels: List<DomainLayerChannels.SKChannel>,
    onClickAdd: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 4.dp, end = 8.dp, bottom = 4.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .testTag("expand_collapse_${expandCollapseModel.title}")
                .clickable {
                    onExpandCollapse(!expandCollapseModel.isOpen)
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = expandCollapseModel.title,
                style = SlackCloneTypography.subtitle2.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.weight(1f)
            )
            AddButton(expandCollapseModel, onClickAdd)
            ToggleButton(expandCollapseModel, onExpandCollapse)
        }
        ChannelsList(expandCollapseModel, onItemClick, channels)
        Divider(color = LocalSlackCloneColor.current.lineColor, thickness = 0.5.dp)
    }
}

@Composable
internal fun ColumnScope.ChannelsList(
    expandCollapseModel: ExpandCollapseModel,
    onItemClick: (DomainLayerChannels.SKChannel) -> Unit = {},
    channels: List<DomainLayerChannels.SKChannel>
) {
    AnimatedVisibility(visible = expandCollapseModel.isOpen) {
        Column {
            repeat(channels.size) {
                val slackChannel = channels[it]
                SlackChannelItem(slackChannel = slackChannel) { skChannel ->
                    onItemClick(skChannel)
                }
            }
        }
    }
}

@Composable
internal fun AddButton(
    expandCollapseModel: ExpandCollapseModel,
    onClickAdd: () -> Unit
) {
    if (expandCollapseModel.needsPlusButton) {
        IconButton(onClick = onClickAdd, modifier = Modifier.testTag("button_add")) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = LocalSlackCloneColor.current.lineColor
            )
        }
    }
}

@Composable
internal fun ToggleButton(
    expandCollapseModel: ExpandCollapseModel,
    onExpandCollapse: (isChecked: Boolean) -> Unit
) {
    IconToggleButton(checked = expandCollapseModel.isOpen, onCheckedChange = {
        onExpandCollapse(it)
    }) {
        Icon(
            imageVector = if (expandCollapseModel.isOpen) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = LocalSlackCloneColor.current.lineColor
        )
    }
}
