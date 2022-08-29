package dev.baseio.android

import dev.baseio.slackclone.App
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import dev.baseio.slackclone.commonui.theme.SlackCloneTheme
import dev.baseio.slackclone.data.DriverFactory
import dev.baseio.slackclone.initKoin

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      SlackCloneTheme {
        App(sqlDriver = DriverFactory(this@MainActivity).createDriver())
      }
    }
  }
}