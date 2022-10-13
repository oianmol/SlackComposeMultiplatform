package dev.baseio.android

import dev.baseio.slackclone.App
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import dev.baseio.database.SlackDB
import dev.baseio.slackclone.LocalWindow
import dev.baseio.slackclone.WindowInfo
import dev.baseio.slackclone.appNavigator
import dev.baseio.slackclone.commonui.theme.SlackCloneTheme
import dev.baseio.slackdata.DriverFactory
import dev.baseio.slackdata.SKKeyValueData
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      val config = LocalConfiguration.current

      var rememberedComposeWindow by remember(this.window) {
        mutableStateOf(WindowInfo(config.screenWidthDp.dp, config.screenHeightDp.dp))
      }

      LaunchedEffect(config) {
        snapshotFlow { config }
          .distinctUntilChanged()
          .onEach {
            rememberedComposeWindow = WindowInfo(it.screenWidthDp.dp, it.screenHeightDp.dp)
          }
          .launchIn(this)
      }

      onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
          appNavigator.navigateUp()
        }
      })
      appNavigator.whenRouteCanNoLongerNavigateBack = {
        finish()
      }
      CompositionLocalProvider(
        LocalWindow provides rememberedComposeWindow
      ) {
        SlackCloneTheme {
          App(
            sqlDriver = DriverFactory(this@MainActivity).createDriver(SlackDB.Schema),
            skKeyValueData = SKKeyValueData(this)
          )
        }
      }

    }
  }
}