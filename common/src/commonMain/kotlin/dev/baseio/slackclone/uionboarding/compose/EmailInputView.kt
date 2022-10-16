package dev.baseio.slackclone.uionboarding.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.commonui.theme.SlackCloneColorProvider
import dev.baseio.slackclone.commonui.theme.SlackCloneTypography
import dev.baseio.slackclone.uionboarding.vm.EmailInputVM

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EmailInputView(modifier: Modifier = Modifier, emailInputVM: EmailInputVM) {
  val email = emailInputVM.email.collectAsState()
  Column(
    modifier = modifier
      .fillMaxWidth()
      .wrapContentWidth()
  ) {
    EmailHeading()
    EmailTF(modifier.fillMaxWidth(), email.value, onChange = {
      emailInputVM.email.value = it
    })
  }

}

@Composable
fun EmailHeading() {
  Text(
    text = "Email", style = SlackCloneTypography.caption.copy(
      color = SlackCloneColorProvider.colors.textPrimary.copy(alpha = 0.7f),
      fontWeight = FontWeight.Normal,
      textAlign = TextAlign.Start
    ), modifier = Modifier.padding(bottom = 4.dp)
  )
}

@Composable
fun PasswordHeading() {
  Text(
    text = "Password", style = SlackCloneTypography.caption.copy(
      color = SlackCloneColorProvider.colors.textPrimary.copy(alpha = 0.7f),
      fontWeight = FontWeight.Normal,
      textAlign = TextAlign.Start
    ), modifier = Modifier.padding(bottom = 4.dp)
  )
}

@ExperimentalComposeUiApi
@Composable
fun EmailTF(modifier: Modifier = Modifier, value: String, onChange: (String) -> Unit) {
  val keyboardController = LocalSoftwareKeyboardController.current
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Start
  ) {
    TextField(
      value = value,
      onValueChange = { newEmail ->
        onChange(newEmail)
      },
      textStyle = textStyleField(),
      leadingIcon = {
        Icon(
          imageVector = Icons.Default.Email,
          contentDescription = null,
          tint = SlackCloneColorProvider.colors.appBarTextTitleColor
        )
      },
      placeholder = {
        Text(
          text = "your email address",
          style = textStyleField(),
          textAlign = TextAlign.Start
        )
      },
      keyboardOptions = KeyboardOptions.Default.copy(
        autoCorrect = false,
        keyboardType = KeyboardType.Email,
        imeAction = ImeAction.Done,
      ),
      keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
      colors = textFieldColors(),
      singleLine = true,
      maxLines = 1
    )
  }


}

@ExperimentalComposeUiApi
@Composable
fun PasswordTF(modifier: Modifier = Modifier, value: String, onChange: (String) -> Unit) {
  val keyboardController = LocalSoftwareKeyboardController.current
  Row(
    modifier = modifier,
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Start
  ) {
    TextField(
      value = value,
      onValueChange = { newEmail ->
        onChange(newEmail)
      },
      textStyle = textStyleField(),
      leadingIcon = {
        Icon(
          imageVector = Icons.Default.Lock,
          contentDescription = null,
          tint = SlackCloneColorProvider.colors.appBarTextTitleColor
        )
      },
      placeholder = {
        Text(
          text = "your password",
          style = textStyleField(),
          textAlign = TextAlign.Start
        )
      },
      keyboardOptions = KeyboardOptions.Default.copy(
        autoCorrect = false,
        keyboardType = KeyboardType.Password,
        imeAction = ImeAction.Done,
      ),
      keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
      colors = textFieldColors(),
      singleLine = true,
      maxLines = 1,
      visualTransformation = PasswordVisualTransformation()
    )
  }


}

@Composable
private fun textFieldColors() = TextFieldDefaults.textFieldColors(
  backgroundColor = Color.Transparent,
  cursorColor = SlackCloneColorProvider.colors.appBarTextTitleColor,
  unfocusedIndicatorColor = Color.Transparent,
  focusedIndicatorColor = Color.Transparent
)

@Composable
private fun textStyleField() = SlackCloneTypography.h6.copy(
  color = SlackCloneColorProvider.colors.appBarTextTitleColor,
  fontWeight = FontWeight.Normal,
  textAlign = TextAlign.Start
)