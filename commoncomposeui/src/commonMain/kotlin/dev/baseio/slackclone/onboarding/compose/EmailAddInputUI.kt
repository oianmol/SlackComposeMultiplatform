package dev.baseio.slackclone.onboarding.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.commonui.theme.LocalSlackCloneColor
import dev.baseio.slackclone.commonui.theme.SlackCloneTypography

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun EmailAddressInputView(modifier: Modifier = Modifier, email: String, onUpdate: (String) -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentWidth()
    ) {
        Text(
            text = "Email", style = SlackCloneTypography.caption.copy(
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
            EmailTF(email) { newEmail ->
                onUpdate(newEmail)
            }
        }
    }

}

@ExperimentalComposeUiApi
@Composable
internal fun EmailTF(email: String, onUpdate: (String) -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current

    TextField(
        value = email,
        onValueChange = { newEmail ->
            onUpdate(newEmail)
        },
        textStyle = emailTFStyle(),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                tint = LocalSlackCloneColor.current.textPrimary
            )
        },
        placeholder = {
            Text(
                text = "Your email address",
                style = emailTFStyle(),
                textAlign = TextAlign.Start
            )
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            autoCorrect = false,
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
        colors = emailTFColors(),
        singleLine = true,
        maxLines = 1
    )
}

@Composable
internal fun emailTFColors() = TextFieldDefaults.textFieldColors(
    backgroundColor = Color.Transparent,
    cursorColor = LocalSlackCloneColor.current.textPrimary,
    unfocusedIndicatorColor = Color.Transparent,
    focusedIndicatorColor = Color.Transparent
)

@Composable
internal fun emailTFStyle() = SlackCloneTypography.h6.copy(
    color = LocalSlackCloneColor.current.textPrimary,
    fontWeight = FontWeight.Normal,
    textAlign = TextAlign.Start
)