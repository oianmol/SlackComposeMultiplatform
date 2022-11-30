package dev.baseio.slackclone

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import dev.baseio.slackclone.uichannels.createsearch.CreateNewChannelUI
import dev.baseio.slackclone.uichannels.createsearch.SearchCreateChannelUI
import dev.baseio.slackclone.uichat.newchat.NewChatThreadScreen
import dev.baseio.slackclone.uidashboard.compose.DashboardUI
import dev.baseio.slackclone.uionboarding.compose.CreateWorkspaceScreen
import dev.baseio.slackclone.uionboarding.compose.GettingStartedUI
import dev.baseio.slackclone.uiqrscanner.QRScannerUI

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun SlackApp(
    modifier: Modifier = Modifier,
    rootComponent: () -> RootComponent
) {
    Children(modifier = modifier, stack = rootComponent().childStack, animation = stackAnimation(fade())) {
        when (val child = it.instance) {
            is Root.Child.CreateWorkspace -> {
                CreateWorkspaceScreen(child.component)
            }
            is Root.Child.GettingStarted -> {
                GettingStartedUI(child.component)
            }
            is Root.Child.DashboardScreen -> {
                DashboardUI(child.component)
            }
            is Root.Child.CreateNewChannel -> {
                CreateNewChannelUI(child.component)
            }
            is Root.Child.NewChatThread -> {
                NewChatThreadScreen(child.component)
            }
            is Root.Child.SearchCreateChannel -> {
                SearchCreateChannelUI(child.component)
            }
            is Root.Child.QrScanner -> {
                QRScannerUI(mode = child.mode, qrCodeDelegate = koinApp.koin.get()) {
                    rootComponent().navigationPop()
                }
            }
        }
    }
}
