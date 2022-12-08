package dev.baseio.slackclone.onboarding.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.commonui.theme.LocalSlackCloneColor
import dev.baseio.slackclone.commonui.theme.SlackCloneTypography

@Composable
internal fun WorkspaceInputView(modifier: Modifier, workspaceUrl: String, onUpdate: (String) -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentWidth()
    ) {
        Text(
            text = "Workspace URL", style = SlackCloneTypography.caption.copy(
                color = LocalSlackCloneColor.current.textPrimary.copy(alpha = 0.7f),
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start
            ), modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(
            modifier = modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            TextHttps()
            WorkspaceTF(workspaceUrl,onUpdate)
            TextSlackCom()
        }
    }
}


@Composable
internal fun WorkspaceTF( workspaceUrl: String, onUpdate: (String) -> Unit) {
    BasicTextField(
        value = workspaceUrl,
        onValueChange = { newEmail -> onUpdate(newEmail)},
        textStyle = textStyleField(),
        singleLine = true,
        modifier = Modifier
            .width(IntrinsicSize.Min)
            .padding(top = 12.dp, bottom = 12.dp),
        maxLines = 1,
        cursorBrush = SolidColor(LocalSlackCloneColor.current.textPrimary),
        decorationBox = { inputTf ->
            Box {
                if (workspaceUrl.isEmpty()) {
                    Text(
                        text = "your-workspace",
                        style = textStyleField(),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.width(IntrinsicSize.Max),
                    )
                } else {
                    inputTf()
                }
            }
        }
    )
}