package dev.baseio.slackclone

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import dev.baseio.slackclone.channels.createsearch.CreateNewChannelUI
import dev.baseio.slackclone.channels.createsearch.SearchCreateChannelUI
import dev.baseio.slackclone.chatmessaging.newchat.NewChatThreadScreen
import dev.baseio.slackclone.dashboard.compose.DashboardUI
import dev.baseio.slackclone.onboarding.compose.EmailAddressInputUI
import dev.baseio.slackclone.onboarding.compose.GettingStartedUI
import dev.baseio.slackclone.onboarding.compose.ProcessEmailWorkspaceSendEmailUI
import dev.baseio.slackclone.onboarding.compose.ProcessAuthTokenScreen
import dev.baseio.slackclone.onboarding.compose.WorkspaceInputUI
import dev.baseio.slackclone.qrscanner.QRScannerUI

@Composable
fun SlackApp(
    modifier: Modifier = Modifier,
    rootComponent: () -> RootComponent
) {
    Children(
        modifier = modifier.notchPadding(),
        stack = rootComponent().childStack,
        animation = stackAnimation(fade())
    ) {
        when (val child = it.instance) {
            is Root.Child.AuthorizeWithToken -> {
                // once we receive the deeplink with token, then we open this composable
                ProcessAuthTokenScreen(child.component)
            }

            is Root.Child.AuthorizeSendEmail -> {
                ProcessEmailWorkspaceSendEmailUI(child.component)
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
                QRScannerUI(mode = child.mode, qrCodeDelegate = getKoin().get()) {
                    rootComponent().navigationPop()
                }
            }

            is Root.Child.EmailMagicLink -> {
                EmailAddressInputUI(navigateBack = {
                    rootComponent().navigationPop()
                }, navigateNext = { email ->
                    rootComponent().navigatePush(RootComponent.Config.SignInManually(email))
                })
            }

            is Root.Child.SignInManually -> {
                WorkspaceInputUI(navigateBack = {
                    rootComponent().navigationPop()
                }, navigateNext = { workspace ->
                    rootComponent().navigatePush(
                        RootComponent.Config.AuthorizeSendEmail(
                            child.emailAddress,
                            workspace
                        )
                    )
                })
            }
        }
    }
}
