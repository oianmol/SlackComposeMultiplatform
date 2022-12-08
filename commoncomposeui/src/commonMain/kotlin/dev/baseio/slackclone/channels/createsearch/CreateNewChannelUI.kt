package dev.baseio.slackclone.channels.createsearch

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.baseio.slackclone.commonui.material.SlackSurfaceAppBar
import dev.baseio.slackclone.commonui.theme.LocalSlackCloneColor
import dev.baseio.slackclone.commonui.theme.SlackCloneSurface
import dev.baseio.slackclone.commonui.theme.SlackCloneTypography
import mainDispatcher

@Composable
internal fun CreateNewChannelUI(
    createNewChannelComponent: CreateNewChannelComponent
) {
    val scaffoldState = rememberScaffoldState()

        Scaffold(
            backgroundColor = LocalSlackCloneColor.current.uiBackground,
            contentColor = LocalSlackCloneColor.current.textSecondary,
            modifier = Modifier,
            scaffoldState = scaffoldState,
            topBar = {
                NewChannelAppBar(createNewChannelComponent)
            },
            snackbarHost = {
                scaffoldState.snackbarHostState
            }
        ) { innerPadding ->
            NewChannelContent(innerPadding, createNewChannelComponent)
        }

}

@Composable
internal fun NewChannelContent(innerPadding: PaddingValues, createNewChannelComponent: CreateNewChannelComponent) {
    Box(modifier = Modifier.padding(innerPadding)) {
        SlackCloneSurface(
            modifier = Modifier.fillMaxSize()
        ) {
            val scroll = rememberScrollState()

            Column(Modifier.verticalScroll(scroll)) {
                Name()
                NameField(createNewChannelComponent)
                Divider(color = LocalSlackCloneColor.current.lineColor)
                Spacer(modifier = Modifier.padding(bottom = 8.dp))
            }
        }
    }
}

@Composable
internal fun Name() {
    Text(
        text = "Name",
        style = textStyleFieldPrimary(),
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
internal fun NameField(createNewChannelComponent: CreateNewChannelComponent) {
    val searchChannel by createNewChannelComponent.viewModel.createChannelState.collectAsState(mainDispatcher)

    TextField(
        value = searchChannel.channel.name,
        onValueChange = { newValue ->
            val newId = newValue.replace(" ", "_")
            with(createNewChannelComponent.viewModel.createChannelState){
                value = value.copy(channel = value.channel.copy(name = newId))
            }
        },
        textStyle = textStyleFieldPrimary(),
        leadingIcon = {
            Text(text = "#", style = textStyleFieldSecondary())
        },
        trailingIcon = {
            Text(text = "${80 - searchChannel.channel.name.length}", style = textStyleFieldSecondary())
        },
        placeholder = {
            Text(
                text = "e.g. plan-budget",
                style = textStyleFieldSecondary(),
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        },
        colors = textFieldColors(),
        singleLine = true,
        maxLines = 1,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
internal fun textStyleFieldPrimary() = SlackCloneTypography.subtitle1.copy(
    color = LocalSlackCloneColor.current.textPrimary,
    fontWeight = FontWeight.Normal,
    textAlign = TextAlign.Start
)

@Composable
internal fun textStyleFieldSecondary() = SlackCloneTypography.subtitle2.copy(
    color = LocalSlackCloneColor.current.textSecondary,
    fontWeight = FontWeight.Normal,
    textAlign = TextAlign.Start
)

@Composable
internal fun textFieldColors() = TextFieldDefaults.textFieldColors(
    backgroundColor = Color.Transparent,
    cursorColor = LocalSlackCloneColor.current.textPrimary,
    unfocusedIndicatorColor = Color.Transparent,
    focusedIndicatorColor = Color.Transparent
)

@Composable
internal fun NewChannelAppBar(
    createNewChannelComponent: CreateNewChannelComponent
) {
    val haptic = LocalHapticFeedback.current
    SlackSurfaceAppBar(
        title = {
            NavTitle()
        },
        navigationIcon = {
            NavBackIcon(createNewChannelComponent)
        },
        backgroundColor = LocalSlackCloneColor.current.appBarColor,
        actions = {
            TextButton(onClick = {
                createNewChannelComponent.viewModel.createChannelState.value.channel.name.takeIf { it.isNotEmpty() }?.let {
                    createNewChannelComponent.viewModel.createChannel()
                } ?: run {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            }) {
                Text(
                    "Create",
                    style = textStyleFieldSecondary().copy(color = LocalSlackCloneColor.current.appBarTextSubTitleColor)
                )
            }
        }
    )
}

@Composable
internal fun NavTitle() {
    Text(
        text = "New Channel",
        style = SlackCloneTypography.subtitle1.copy(color = LocalSlackCloneColor.current.appBarTextTitleColor)
    )
}

@Composable
internal fun NavBackIcon(createNewChannelComponent: CreateNewChannelComponent) {
    IconButton(onClick = {
        createNewChannelComponent.navigationPop()
    }) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Clear",
            modifier = Modifier.padding(start = 8.dp),
            tint = LocalSlackCloneColor.current.appBarIconColor
        )
    }
}
