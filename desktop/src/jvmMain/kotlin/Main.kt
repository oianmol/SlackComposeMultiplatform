import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.github.sarxos.winreg.HKey
import com.github.sarxos.winreg.WindowsRegistry
import dev.baseio.slackclone.RootComponent
import dev.baseio.slackclone.SlackApp
import dev.baseio.slackclone.commonui.theme.SlackCloneTheme
import dev.baseio.slackclone.initKoin
import java.awt.Desktop

@ExperimentalComposeUiApi
fun main() {
    application {
        val windowState = rememberWindowState()
        val lifecycle = LifecycleRegistry()

        val rootComponent by lazy { RootComponent(DefaultComponentContext(lifecycle = lifecycle)) }
        initKoin()

        handleDeepLink(rootComponent)

        Window(onCloseRequest = ::exitApplication, state = windowState) {
            DesktopApp() {
                rootComponent
            }
        }
    }
}

private fun handleDeepLink(rootComponent: RootComponent) {
    try {
        val osName = System.getProperty("os.name")
        if (osName.equals("windows", ignoreCase = true)) {
            val reg: WindowsRegistry = WindowsRegistry.getInstance()
            val appExecutablePath = System.getProperty("java.home")

            val protocolRegKey = "Software\\Classes\\slackclone"
            val protocolCmdRegKey = "shell\\open\\command"

            reg.createKey(HKey.HKCU, protocolRegKey)
            reg.createKey(HKey.HKCU, protocolCmdRegKey)

            reg.writeStringValue(HKey.HKCU, protocolRegKey, "URL Protocol", "")
            reg.writeStringValue(HKey.HKCU, protocolCmdRegKey, "", "$appExecutablePath %1")
        }
        Desktop.getDesktop().setOpenURIHandler { event ->
            val queryMap = UriUtil.parseQueryMap(event.uri)
            queryMap["channelId"]?.let { channelId ->
                queryMap["workspaceId"]?.let { workspaceId ->
                    rootComponent.navigateChannel(
                        channelId,
                        workspaceId
                    )
                }
            }
            queryMap["token"]?.let { token ->
                rootComponent.navigateAuthorizeWithToken(token)
            }
        }
    } catch (e: UnsupportedOperationException) {
        println("setOpenURIHandler is unsupported")
    }
}

@Composable
fun DesktopApp(
    rootComponent: () -> RootComponent
) {
    SlackCloneTheme {
        SlackApp(
            rootComponent = rootComponent
        )
    }
}
