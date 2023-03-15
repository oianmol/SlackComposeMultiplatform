package dev.baseio.slackclone.commonui.reusable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.commonui.theme.LocalSlackCloneColor
import dev.baseio.slackclone.commonui.theme.SlackCloneTypography

@Composable
internal fun SlackListItem(
    modifier: Modifier = Modifier.fillMaxWidth(),
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    textColor: Color = LocalSlackCloneColor.current.textPrimary,
    trailingItem: ImageVector? = null,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    onItemClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .padding(8.dp)
            .clickable {
                onItemClick()
            },
        verticalAlignment = verticalAlignment,
        horizontalArrangement = horizontalArrangement
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor.copy(alpha = 0.4f),
            modifier = Modifier
                .size(24.dp)
                .padding(4.dp)
        )
        subtitle?.let {
            Column {
                TitleText(title, textColor)
                SubTitleText(it, textColor)
            }
        } ?: run {
            TitleText(title, textColor)
        }
        trailingItem?.let { safeIcon ->
            Icon(
                imageVector = safeIcon,
                contentDescription = null,
                tint = LocalSlackCloneColor.current.textPrimary.copy(alpha = 0.4f),
                modifier = Modifier
                    .size(24.dp)
                    .padding(4.dp)
            )
        }
    }
}


@Composable
internal fun SubTitleText(title: String, textColor: Color) {
    Text(
        text = title,
        style = SlackCloneTypography.caption.copy(
            color = textColor.copy(
                alpha = 0.5f
            )
        ),
        modifier = Modifier
            .padding(8.dp)
    )
}

@Composable
internal fun TitleText(title: String, textColor: Color) {
    Text(
        text = title,
        style = SlackCloneTypography.subtitle2.copy(
            color = textColor.copy(
                alpha = 0.8f
            )
        ),
        modifier = Modifier
            .padding(8.dp)
    )
}

@Composable
internal fun SlackListItem(
    modifier: Modifier = Modifier.padding(8.dp),
    icon: @Composable () -> Unit,
    center: @Composable (Modifier) -> Unit,
    trailingItem: @Composable () -> Unit? = {},
    onItemClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .clickable {
                onItemClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()

        center(Modifier)

        trailingItem()
    }
}
