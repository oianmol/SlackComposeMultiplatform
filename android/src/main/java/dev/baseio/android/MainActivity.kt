package dev.baseio.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.DefaultComponentContext
import dev.baseio.database.SlackDB
import dev.baseio.slackclone.*
import dev.baseio.slackclone.commonui.theme.SlackCloneTheme
import dev.baseio.slackdata.DriverFactory
import dev.baseio.slackdata.SKKeyValueData
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)


    val defaultComponentContext = DefaultComponentContext(
      lifecycle = lifecycle,
      savedStateRegistry = savedStateRegistry,
      viewModelStore = viewModelStore,
      onBackPressedDispatcher = onBackPressedDispatcher,
    )
    val skKeyValueData =  SKKeyValueData(this)
    val root by lazy {
      RootComponent(defaultComponentContext, skKeyValueData)
    }

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

      CompositionLocalProvider(
        LocalWindow provides rememberedComposeWindow
      ) {
        SlackCloneTheme {
          App(
            sqlDriver = DriverFactory(this@MainActivity).createDriver(SlackDB.Schema),
            skKeyValueData = skKeyValueData,
            rootComponent = {
              root
            }
          )
        }
      }

    }
  }
}