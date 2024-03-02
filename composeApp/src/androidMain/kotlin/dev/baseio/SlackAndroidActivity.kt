package dev.baseio.android

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.arkivanov.decompose.DefaultComponentContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import dev.baseio.android.util.showToast
import dev.baseio.slackclone.RootComponent
import dev.baseio.slackclone.SlackApp
import dev.baseio.slackclone.commonui.theme.SlackCloneTheme

class SlackAndroidActivity : AppCompatActivity() {


    private val rootComponent by lazy {
        RootComponent(DefaultComponentContext(
            lifecycle = lifecycle,
            savedStateRegistry = savedStateRegistry,
            viewModelStore = viewModelStore,
            onBackPressedDispatcher = onBackPressedDispatcher
        ))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SlackCloneTheme {
                SlackApp {
                    rootComponent
                }
            }
            ChannelNavigator(rootComponent)
            AuthNavigator(rootComponent)
            askForPostNotificationPermission()
        }
    }

    @Composable
    private fun AuthNavigator(root: RootComponent) {
        LaunchedEffect(intent?.action) {
            intent?.action?.let {
                if (it == Intent.ACTION_VIEW) {
                    intent.data?.getQueryParameter("token")?.takeIf { token -> token.isNotEmpty() }
                        ?.let { token ->
                            root.navigateAuthorizeWithToken(token)
                        }
                }
            }
        }
    }

    @Composable
    private fun ChannelNavigator(root: RootComponent) {
        LaunchedEffect(intent?.channelId()) {
            with(intent) {
                channelId()?.let {
                    root.navigateChannel(channelId()!!, workspaceId()!!)
                }
            }
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

    companion object {
        fun channelChatIntent(channelId: String, workspaceId: String, context: Context): Intent {
            return Intent(context, SlackAndroidActivity::class.java).apply {
                putExtra(EXTRA_CHANNEL_ID, channelId)
                putExtra(EXTRA_WORKSPACE_ID, workspaceId)
            }
        }

        const val EXTRA_CHANNEL_ID: String = "channel_id"
        const val EXTRA_WORKSPACE_ID = "workspaceId"
        const val INTENT_KEY_NOT_ID: String = "notification_id"
    }

    private fun Intent.channelId() = this.extras?.getString(EXTRA_CHANNEL_ID)
    private fun Intent.workspaceId() = this.extras?.getString(EXTRA_WORKSPACE_ID)
}