package dev.baseio.slackclone.uichannels.createsearch

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
import dev.baseio.slackclone.commonui.theme.SlackCloneColorProvider
import dev.baseio.slackclone.commonui.theme.SlackCloneSurface
import dev.baseio.slackclone.commonui.theme.SlackCloneTypography
import mainDispatcher

@Composable
fun CreateNewChannelUI(
    createNewChannelComponent: CreateNewChannelComponent
) {
    val scaffoldState = rememberScaffoldState()
    Box {
        Scaffold(
            backgroundColor = SlackCloneColorProvider.colors.uiBackground,
            contentColor = SlackCloneColorProvider.colors.textSecondary,
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
}

@Composable
private fun NewChannelContent(innerPadding: PaddingValues, createNewChannelComponent: CreateNewChannelComponent) {
    Box(modifier = Modifier.padding(innerPadding)) {
        SlackCloneSurface(
            modifier = Modifier.fillMaxSize()
        ) {
            val scroll = rememberScrollState()

            Column(Modifier.verticalScroll(scroll)) {
                Name()
                NameField(createNewChannelComponent)
                Divider(color = SlackCloneColorProvider.colors.lineColor)
                Spacer(modifier = Modifier.padding(bottom = 8.dp))
            }
        }
    }
}

@Composable
private fun Name() {
    Text(
        text = "Name",
        style = textStyleFieldPrimary(),
        modifier = Modifier.padding(8.dp)
    )
}

@Composable
private fun NameField(createNewChannelComponent: CreateNewChannelComponent) {
    val searchChannel by createNewChannelComponent.viewModel.createChannelState.collectAsState(mainDispatcher)

    TextField(
        value = searchChannel.name,
        onValueChange = { newValue ->
            val newId = newValue.replace(" ", "_")
            createNewChannelComponent.viewModel.createChannelState.value =
                createNewChannelComponent.viewModel.createChannelState.value.copy(name = newId, uuid = newId)
        },
        textStyle = textStyleFieldPrimary(),
        leadingIcon = {
            Text(text = "#", style = textStyleFieldSecondary())
        },
        trailingIcon = {
            Text(text = "${80 - searchChannel.name.length}", style = textStyleFieldSecondary())
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
private fun textStyleFieldPrimary() = SlackCloneTypography.subtitle1.copy(
    color = SlackCloneColorProvider.colors.textPrimary,
    fontWeight = FontWeight.Normal,
    textAlign = TextAlign.Start
)

@Composable
private fun textStyleFieldSecondary() = SlackCloneTypography.subtitle2.copy(
    color = SlackCloneColorProvider.colors.textSecondary,
    fontWeight = FontWeight.Normal,
    textAlign = TextAlign.Start
)

@Composable
private fun textFieldColors() = TextFieldDefaults.textFieldColors(
    backgroundColor = Color.Transparent,
    cursorColor = SlackCloneColorProvider.colors.textPrimary,
    unfocusedIndicatorColor = Color.Transparent,
    focusedIndicatorColor = Color.Transparent
)

@Composable
private fun NewChannelAppBar(
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
        backgroundColor = SlackCloneColorProvider.colors.appBarColor,
        actions = {
            TextButton(onClick = {
                createNewChannelComponent.viewModel.createChannelState.value.name.takeIf { it.isNotEmpty() }?.let {
                    createNewChannelComponent.viewModel.createChannel()
                } ?: run {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            }) {
                Text(
                    "Create",
                    style = textStyleFieldSecondary().copy(color = SlackCloneColorProvider.colors.appBarTextSubTitleColor)
                )
            }
        }
    )
}

@Composable
private fun NavTitle() {
    Text(
        text = "New Channel",
        style = SlackCloneTypography.subtitle1.copy(color = SlackCloneColorProvider.colors.appBarTextTitleColor)
    )
}

@Composable
private fun NavBackIcon(createNewChannelComponent: CreateNewChannelComponent) {
    IconButton(onClick = {
        createNewChannelComponent.navigationPop()
    }) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Clear",
            modifier = Modifier.padding(start = 8.dp),
            tint = SlackCloneColorProvider.colors.appBarIconColor
        )
    }
}
