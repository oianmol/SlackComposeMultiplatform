import androidx.compose.foundation.background // ktlint-disable import-ordering
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Application
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.destroy
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import dev.baseio.database.SlackDB
import dev.baseio.slackclone.App
import dev.baseio.slackclone.RootComponent
import dev.baseio.slackclone.WindowInfo
import dev.baseio.slackclone.LocalWindow
import dev.baseio.slackclone.commonui.theme.SlackCloneColorProvider
import dev.baseio.slackclone.commonui.theme.SlackCloneTheme
import dev.baseio.slackclone.initKoin
import dev.baseio.slackdata.DriverFactory
import dev.baseio.slackdata.SKKeyValueData
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.cstr
import kotlinx.cinterop.autoreleasepool
import kotlinx.cinterop.useContents
import kotlinx.cinterop.toCValues
import platform.Foundation.NSStringFromClass
import platform.UIKit.UIApplication
import platform.UIKit.UIResponder
import platform.UIKit.UIApplicationDelegateProtocol
import platform.UIKit.UIResponderMeta
import platform.UIKit.UIApplicationDelegateProtocolMeta
import platform.UIKit.UIWindow
import platform.UIKit.UIInterfaceOrientationMask
import platform.UIKit.UIInterfaceOrientationMaskAll
import platform.UIKit.UIScreen
import platform.UIKit.UIApplicationMain
import org.koin.dsl.module
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Application
import platform.UIKit.UIViewController

val lifecycle = LifecycleRegistry()
val skKeyValueData = SKKeyValueData()
val root by lazy {
    RootComponent(
        context = DefaultComponentContext(lifecycle = lifecycle),
    )
}

fun MainViewController(window:UIWindow): UIViewController =
    Application("SlackComposeiOS") {
        val rememberedComposeWindow by remember(window) {
            val windowInfo = window.frame.useContents {
                WindowInfo(this.size.width.dp, this.size.height.dp)
            }
            mutableStateOf(windowInfo)
        }
        initKoin()

        CompositionLocalProvider(
            LocalWindow provides rememberedComposeWindow
        ) {
            SlackCloneTheme(isDarkTheme = true) {
                Column {
                    Box(Modifier.height(48.dp).background(SlackCloneColorProvider.colors.appBarColor))
                    App {
                        root
                    }
                }
            }
        }
    }
