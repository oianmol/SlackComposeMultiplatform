package dev.baseio.slackclone

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.squareup.sqldelight.db.SqlDriver
import dev.baseio.database.SlackDB
import dev.baseio.slackclone.chatcore.injection.uiModelMapperModule
import dev.baseio.slackclone.common.injection.dispatcherModule
import dev.baseio.slackclone.data.DriverFactory
import dev.baseio.slackclone.data.injection.dataMappersModule
import dev.baseio.slackclone.data.injection.repositoryModule
import dev.baseio.slackclone.data.injection.useCaseModule
import dev.baseio.slackclone.data.injection.viewModelModule
import dev.baseio.slackclone.navigation.Navigator
import dev.baseio.slackclone.navigation.SlackScreens
import dev.baseio.slackclone.navigation.screen
import dev.baseio.slackclone.uidashboard.compose.DashboardVM
import dev.baseio.slackclone.uidashboard.nav.DashboardNavigation
import dev.baseio.slackclone.uionboarding.compose.EmailAddressInputUI
import dev.baseio.slackclone.uionboarding.compose.GettingStartedUI
import dev.baseio.slackclone.uionboarding.compose.SkipTypingUI
import dev.baseio.slackclone.uionboarding.compose.WorkspaceInputUI
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module


var koinApp: KoinApplication? = null

@Composable
fun App(modifier: Modifier = Modifier, sqlDriver: SqlDriver) {
  if (koinApp == null) {
    koinApp = initKoin(SlackDB.invoke(sqlDriver))
  }
  Box(modifier) {
    Navigator(initialScreen = SlackScreens.GettingStarted) {
      screen(SlackScreens.GettingStarted) {
        GettingStartedUI(this)
      }
      screen(SlackScreens.SkipTypingScreen) {
        SkipTypingUI(this)
      }
      screen(SlackScreens.WorkspaceInputUI) {
        WorkspaceInputUI(this)
      }
      screen(SlackScreens.EmailAddressInputUI) {
        EmailAddressInputUI(this)
      }
      screen(SlackScreens.DashboardNavigation) {
        DashboardNavigation()
      }
    }
  }
}

fun initKoin(slackDB: SlackDB): KoinApplication {
  return startKoin {
    modules(module {
      single { slackDB }
    }, repositoryModule, dataMappersModule, useCaseModule, viewModelModule, uiModelMapperModule, dispatcherModule)
  }
}
