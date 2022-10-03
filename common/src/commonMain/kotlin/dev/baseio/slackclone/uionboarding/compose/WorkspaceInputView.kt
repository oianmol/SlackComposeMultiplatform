package dev.baseio.slackclone.uionboarding.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.commonui.theme.SlackCloneColorProvider
import dev.baseio.slackclone.commonui.theme.SlackCloneTypography
import dev.baseio.slackclone.uionboarding.vm.WorkspaceInputVM

@Composable
fun WorkspaceInputView(modifier: Modifier, workspaceInputVM: WorkspaceInputVM) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .wrapContentWidth()
  ) {
    Text(
      text = "Workspace URL", style = SlackCloneTypography.caption.copy(
        color = SlackCloneColorProvider.colors.textPrimary.copy(alpha = 0.7f),
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
      WorkspaceTF(workspaceInputVM)
      TextSlackCom()
    }
  }
}

@Composable
private fun TextHttps() {
  Text(
    text = "https://",
    style = textStyleField().copy(
      color = SlackCloneColorProvider.colors.textPrimary.copy(
        alpha = 0.4f
      )
    )
  )
}

@Composable
private fun TextSlackCom() {
  Text(
    ".slack.com",
    style = textStyleField().copy(
      color = SlackCloneColorProvider.colors.textPrimary.copy(
        alpha = 0.4f
      )
    ),
    overflow = TextOverflow.Clip,
    maxLines = 1
  )
}


@Composable
private fun WorkspaceTF(workspaceInputVM: WorkspaceInputVM) {
  val workspace by workspaceInputVM.workspace.collectAsState()

  BasicTextField(
    value = workspace,
    onValueChange = { newEmail ->
      workspaceInputVM.workspace.value = newEmail
    },
    textStyle = textStyleField(),
    singleLine = true,
    modifier = Modifier
      .width(IntrinsicSize.Min)
      .padding(top = 12.dp, bottom = 12.dp),
    maxLines = 1,
    cursorBrush = SolidColor(SlackCloneColorProvider.colors.textPrimary),
    decorationBox = { inputTf ->
      Box {
        if (workspace.isEmpty()) {
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

@Composable
private fun textFieldColors() = TextFieldDefaults.textFieldColors(
  backgroundColor = Color.Transparent,
  cursorColor = SlackCloneColorProvider.colors.textPrimary,
  unfocusedIndicatorColor = Color.Transparent,
  focusedIndicatorColor = Color.Transparent
)

@Composable
private fun textStyleField() = SlackCloneTypography.h6.copy(
  color = SlackCloneColorProvider.colors.textPrimary.copy(alpha = 0.7f),
  fontWeight = FontWeight.Normal,
  textAlign = TextAlign.Start
)