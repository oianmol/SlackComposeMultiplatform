package dev.baseio.android

import dev.baseio.slackclone.App
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import dev.baseio.slackclone.LocalWindow
import dev.baseio.slackclone.WindowInfo
import dev.baseio.slackclone.commonui.theme.SlackCloneTheme
import dev.baseio.slackclone.data.DriverFactory

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      val config = LocalConfiguration.current
      val rememberedComposeWindow = remember {
        WindowInfo(config.screenWidthDp, config.screenHeightDp)
      }
      CompositionLocalProvider(
        LocalWindow provides rememberedComposeWindow
      ) {
        SlackCloneTheme {
          App(sqlDriver = DriverFactory(this@MainActivity).createDriver())
        }
      }

    }
  }
}