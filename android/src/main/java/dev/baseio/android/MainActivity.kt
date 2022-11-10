package dev.baseio.android

import android.os.Bundle // ktlint-disable import-ordering
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.DefaultComponentContext
import dev.baseio.database.SlackDB
import dev.baseio.security.AndroidKeyStoreRsaUtils
import dev.baseio.security.AndroidSecurityProvider
import dev.baseio.security.HybridRsaUtils
import dev.baseio.security.RsaEcdsaConstants
import dev.baseio.security.RsaEcdsaKeyManager
import dev.baseio.slackclone.App
import dev.baseio.slackclone.LocalWindow
import dev.baseio.slackclone.RootComponent
import dev.baseio.slackclone.WindowInfo
import dev.baseio.slackclone.commonui.theme.SlackCloneTheme
import dev.baseio.slackclone.initKoin
import dev.baseio.slackdata.DriverFactory
import dev.baseio.slackdata.SKKeyValueData
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.example.android.R
import org.koin.core.KoinApplication
import org.koin.dsl.module
import java.security.PublicKey

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSecurityProvider.initialize(this)
        val rsaEcdsaKeyManager = RsaEcdsaKeyManager(
            senderVerificationKey = this@MainActivity.resources.openRawResource(R.raw.sender_verification_key).readBytes(),
            chainId = "1"
        )

        val defaultComponentContext = DefaultComponentContext(
            lifecycle = lifecycle,
            savedStateRegistry = savedStateRegistry,
            viewModelStore = viewModelStore,
            onBackPressedDispatcher = onBackPressedDispatcher
        )
        val skKeyValueData = SKKeyValueData(this)
        val root by lazy {
            RootComponent(defaultComponentContext)
        }

        val koinApplication =
            initKoin(
                module {
                    single { skKeyValueData }
                    single { SlackDB.invoke(DriverFactory(this@MainActivity).createDriver(SlackDB.Schema)) }
                    single {
                        rsaEcdsaKeyManager
                    }
                }
            )

        setContent {
            MobileApp({
                root
            }, koinApplication)
        }
    }
}

@Composable
fun MobileApp(root: () -> RootComponent, koinApplication: KoinApplication) {
    val config = LocalConfiguration.current

    var rememberedComposeWindow by remember {
        mutableStateOf(WindowInfo(config.screenWidthDp.dp, config.screenHeightDp.dp))
    }

    LaunchedEffect(config) {
        snapshotFlow { config }.distinctUntilChanged().onEach {
            rememberedComposeWindow = WindowInfo(it.screenWidthDp.dp, it.screenHeightDp.dp)
        }.launchIn(this)
    }

    CompositionLocalProvider(
        LocalWindow provides rememberedComposeWindow
    ) {
        SlackCloneTheme {
            App(
                rootComponent = {
                    root.invoke()
                }, koinApplication = koinApplication
            )
        }
    }
}
