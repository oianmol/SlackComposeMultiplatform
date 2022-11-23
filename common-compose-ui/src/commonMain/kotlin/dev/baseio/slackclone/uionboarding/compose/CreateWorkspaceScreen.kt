package dev.baseio.slackclone.uionboarding.compose

import PainterRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import dev.baseio.slackclone.LocalWindow
import dev.baseio.slackclone.commonui.theme.*
import dev.baseio.slackclone.uidashboard.compose.WindowSize
import dev.baseio.slackclone.uidashboard.compose.getWindowSizeClass
import dev.baseio.slackclone.uionboarding.vm.CreateWorkspaceComponent
import mainDispatcher

@Composable
internal fun CreateWorkspaceScreen(
    component: CreateWorkspaceComponent
) {
    val scaffoldState = rememberScaffoldState()
    val size = getWindowSizeClass(LocalWindow.current)
    PlatformSideEffects.GettingStartedScreen()

    Scaffold(
        backgroundColor = SlackCloneColor,
        contentColor = LocalSlackCloneColor.current.textSecondary,
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        snackbarHost = {
            scaffoldState.snackbarHostState
        }
    ) { innerPadding ->
        SlackCloneSurface(
            color = SlackCloneColor,
            modifier = Modifier
                .padding(innerPadding)
        ) {
            when (size) {
                WindowSize.Phones -> CreateWorkspacePhoneLayout(component)
                else -> {
                    CreateWorkspaceLargeScreenLayout(component, contentPadding = Modifier.padding(12.dp))
                }
            }
        }
    }
}

@Composable
internal fun Title(title: String) {
    Text(
        title,
        style = SlackCloneTypography.h5.copy(
            fontWeight = FontWeight.Bold,
            color = LocalSlackCloneColor.current.appBarTextTitleColor
        )
    )
}

@Composable
internal fun SubTitle(text: String) {
    Text(
        text,
        style = SlackCloneTypography.h6.copy(color = LocalSlackCloneColor.current.appBarTextSubTitleColor)
    )
}

@Composable
internal fun Heading(isLogin: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Start) {
        Image(
            modifier = Modifier.size(128.dp),
            painter = PainterRes.slackLogo(),
            contentDescription = null
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            Title(title = if (isLogin) "Login to Slack" else "Create a new workspace")
            SubTitle(text = if (isLogin) "Please provide your credentials" else "To make a workspace from scratch, please confirm your email address.")
        }
    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun WorkspaceCreateForm(createWorkspaceComponent: CreateWorkspaceComponent) {
    val state by createWorkspaceComponent.authCreateWorkspaceVM.state.collectAsState(mainDispatcher)


    Column(Modifier.verticalScroll(rememberScrollState())) {
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
                text = if (createWorkspaceComponent.isLogin()) "Let me in..." else "Create Workspace"
            ) {
                createWorkspaceComponent.authCreateWorkspaceVM.createWorkspace()
            }
        }
    }
}

@Composable
internal fun ErrorText(modifier: Modifier, error: Throwable?) {
    Text(error?.stackTraceToString()?:"", style = SlackCloneTypography.subtitle1.copy(color = Color.White), modifier = modifier)
}

@Composable
internal fun WorkspaceView(modifier: Modifier, name: String, viewModel: CreateWorkspaceComponent) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.padding(horizontal = 12.dp))
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
internal fun CreateWorkspaceButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = {
            onClick.invoke()
        },
        Modifier
            .fillMaxWidth()
            .height(40.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = SlackGreen)
    ) {
        Text(
            text = text,
            style = SlackCloneTypography.subtitle1.copy(color = Color.White, fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
internal fun CreateWorkspaceLargeScreenLayout(viewModel: CreateWorkspaceComponent, contentPadding: Modifier) {
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
            Image(
                modifier = Modifier.size(256.dp),
                painter = PainterRes.gettingStarted(),
                contentDescription = null,
                contentScale = ContentScale.Fit
            )
        }
        Column(
            contentPadding.weight(1f, fill = true).fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Heading(viewModel.isLogin())
            WorkspaceCreateForm(viewModel)
        }

    }
}

@Composable
internal fun CreateWorkspacePhoneLayout(viewModel: CreateWorkspaceComponent) {
    Column(
        Modifier.padding(8.dp).fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Heading(viewModel.isLogin())
        Spacer(Modifier.height(24.dp))
        WorkspaceCreateForm(viewModel)
    }
}
