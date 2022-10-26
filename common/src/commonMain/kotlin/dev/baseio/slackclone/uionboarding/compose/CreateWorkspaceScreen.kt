package dev.baseio.slackclone.uionboarding.compose

import PainterRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.arkivanov.decompose.value.reduce
import dev.baseio.slackclone.LocalWindow
import dev.baseio.slackclone.commonui.theme.*
import dev.baseio.slackclone.uidashboard.compose.WindowSize
import dev.baseio.slackclone.uidashboard.compose.getWindowSizeClass
import dev.baseio.slackclone.uionboarding.vm.CreateWorkspaceComponent

@Composable
fun CreateWorkspaceScreen(
    component: CreateWorkspaceComponent
) {
    val scaffoldState = rememberScaffoldState()
    val size = getWindowSizeClass(LocalWindow.current)
    PlatformSideEffects.GettingStartedScreen()

    Scaffold(
        backgroundColor = SlackCloneColor,
        contentColor = SlackCloneColorProvider.colors.textSecondary,
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        snackbarHost = {
            scaffoldState.snackbarHostState
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            SlackCloneSurface(
                color = SlackCloneColor,
                modifier = Modifier
                    .padding(28.dp)
            ) {
                when (size) {
                    WindowSize.Phones -> CreateWorkspacePhoneLayout(component)
                    else -> {
                        CreateWorkspaceLargeScreenLayout(component)
                    }
                }
            }
        }
    }
}

@Composable
fun Title(title: String) {
    Text(
        title,
        style = SlackCloneTypography.h5.copy(
            fontWeight = FontWeight.Bold,
            color = SlackCloneColorProvider.colors.appBarTextTitleColor
        )
    )
}

@Composable
fun SubTitle(text: String) {
    Text(
        text,
        style = SlackCloneTypography.h6.copy(color = SlackCloneColorProvider.colors.appBarTextSubTitleColor)
    )
}

@Composable
fun Heading(isLogin: Boolean) {
    Column(Modifier.fillMaxWidth()) {
        Title(title = if (isLogin) "Login to Slack" else "Create a new workspace")
        SubTitle(text = if (isLogin) "Please provide your credentials" else "To make a workspace from scratch, please confirm your email address.")
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WorkspaceCreateForm(createWorkspaceComponent: CreateWorkspaceComponent) {
    val state by createWorkspaceComponent.authCreateWorkspaceVM.state.collectAsState()


    Column {
        EmailTF(Modifier.padding(4.dp), state.email) { emailNew ->
            createWorkspaceComponent.authCreateWorkspaceVM.state.apply {
                this.value = this.value.copy(email = emailNew)
            }
        }
        PasswordTF(Modifier.padding(4.dp), state.password) { passwordNew ->
            createWorkspaceComponent.authCreateWorkspaceVM.state.apply {
                this.value = this.value.copy(password = passwordNew)
            }
        }
        WorkspaceView(Modifier.padding(4.dp), state.domain, createWorkspaceComponent)
        Spacer(Modifier.size(4.dp))
        ErrorText(Modifier.padding(4.dp), state.error)
        if (state.loading) {
            CircularProgressIndicator(color = SlackGreen)
        } else {
            CreateWorkspaceButton(
                createWorkspaceComponent
            )
        }
    }
}

@Composable
fun ErrorText(modifier: Modifier, error: Throwable?) {
    Text(error?.message ?: "", style = SlackCloneTypography.subtitle1.copy(color = Color.White), modifier = modifier)
}

@Composable
private fun WorkspaceView(modifier: Modifier, name: String, viewModel: CreateWorkspaceComponent) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.padding(8.dp))
        TextHttps()
        WorkspaceTF(name) { nameNew ->
            viewModel.authCreateWorkspaceVM.state.apply {
                this.value = this.value.copy(domain = nameNew)
            }
        }
        TextSlackCom()
    }
}

@Composable
fun CreateWorkspaceButton(viewModel: CreateWorkspaceComponent) {
    Button(
        onClick = {
            viewModel.authCreateWorkspaceVM.createWorkspace()
        },
        Modifier
            .fillMaxWidth()
            .height(40.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = SlackGreen)
    ) {
        Text(
            text = if (viewModel.isLogin()) "Let me in..." else "Create Workspace",
            style = SlackCloneTypography.subtitle1.copy(color = Color.White, fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun CreateWorkspaceLargeScreenLayout(viewModel: CreateWorkspaceComponent) {
    Row(
        Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            Modifier.weight(1f, fill = true).padding(8.dp).fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Heading(viewModel.isLogin())
            WorkspaceCreateForm(viewModel)
        }
        Column(
            Modifier.weight(1f, fill = true).padding(8.dp).fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.size(256.dp),
                painter = PainterRes.gettingStarted(),
                contentDescription = null,
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun CreateWorkspacePhoneLayout(viewModel: CreateWorkspaceComponent) {
    Column(
        Modifier.padding(8.dp).fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Heading(viewModel.isLogin())
        WorkspaceCreateForm(viewModel)
    }
}
