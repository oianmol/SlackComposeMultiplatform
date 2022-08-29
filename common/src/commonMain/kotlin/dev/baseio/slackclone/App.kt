package dev.baseio.slackclone

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import dev.baseio.slackclone.navigation.Navigator
import dev.baseio.slackclone.navigation.SlackScreens
import dev.baseio.slackclone.navigation.screen
import dev.baseio.slackclone.uidashboard.compose.DashboardVM
import dev.baseio.slackclone.uidashboard.nav.DashboardNavigation
import dev.baseio.slackclone.uionboarding.compose.EmailAddressInputUI
import dev.baseio.slackclone.uionboarding.compose.GettingStartedUI
import dev.baseio.slackclone.uionboarding.compose.SkipTypingUI
import dev.baseio.slackclone.uionboarding.compose.WorkspaceInputUI
import org.koin.core.context.startKoin
import org.koin.dsl.module

@Composable
fun App(modifier: Modifier = Modifier) {
  initKoin()
  Box(modifier){
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
      screen(SlackScreens.DashboardNavigation){
        DashboardNavigation()
      }
    }
  }
}

fun initKoin() {
  startKoin{
    module {
      appModule()
    }
  }
}

val viewModelModules = module {
  single { DashboardVM() }
}

fun appModule()  = listOf(viewModelModules,)
