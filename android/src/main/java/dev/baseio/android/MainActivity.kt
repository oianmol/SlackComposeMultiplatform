package dev.baseio.android

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.DefaultComponentContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import dev.baseio.android.util.showToast
import dev.baseio.slackclone.App
import dev.baseio.slackclone.LocalWindow
import dev.baseio.slackclone.RootComponent
import dev.baseio.slackclone.WindowInfo
import dev.baseio.slackclone.commonui.theme.SlackCloneTheme
import dev.baseio.slackclone.fcmToken
import dev.baseio.slackdomain.usecases.auth.UseCaseSaveFCMToken
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.example.android.R
import org.koin.core.KoinApplication

class MainActivity : AppCompatActivity() {

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val defaultComponentContext = DefaultComponentContext(
            lifecycle = lifecycle,
            savedStateRegistry = savedStateRegistry,
            viewModelStore = viewModelStore,
            onBackPressedDispatcher = onBackPressedDispatcher
        )
        val root by lazy {
            RootComponent(defaultComponentContext)
        }
        setContent {
            askForPostNotificationPermission()
            MobileApp({
                root
            }, (application as SlackApp).koinApplication)
        }
    }

    @SuppressLint("ComposableNaming", "InlinedApi")
    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    private fun askForPostNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val context = LocalContext.current
            val notificationPermission =
                rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS) { permissionGranted ->
                    if (!permissionGranted) {
                        showToast(
                            msg = context.getString(R.string.post_notification_permission_not_allowed_msg),
                            isLongToast = true
                        )
                    }
                }
            LaunchedEffect(Unit) {
                if (!notificationPermission.status.isGranted) {
                    notificationPermission.launchPermissionRequest()
                }
            }
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
