package dev.baseio.slackclone.onboarding.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.commonui.theme.LocalSlackCloneColor
import dev.baseio.slackclone.commonui.theme.SlackCloneTypography

@Composable
internal fun TextHttps() {
    Text(
        text = "https://",
        style = textStyleField().copy(
            color = LocalSlackCloneColor.current.textPrimary.copy(
                alpha = 0.4f
            )
        )
    )
}

@Composable
internal fun TextSlackCom() {
    Text(
        ".slack.com",
        style = textStyleField().copy(
            color = LocalSlackCloneColor.current.textPrimary.copy(
                alpha = 0.4f
            )
        ),
        overflow = TextOverflow.Clip,
        maxLines = 1
    )
}

@Composable
internal fun textStyleField() = SlackCloneTypography.subtitle1.copy(
    color = LocalSlackCloneColor.current.textPrimary.copy(alpha = 0.7f),
    fontWeight = FontWeight.Normal,
    textAlign = TextAlign.Start
)
