package dev.baseio.slackclone

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.squareup.sqldelight.db.SqlDriver
import dev.baseio.database.SlackDB
import dev.baseio.slackclone.chatcore.injection.uiModelMapperModule
import dev.baseio.slackclone.common.injection.dispatcherModule
import dev.baseio.slackclone.data.injection.dataMappersModule
import dev.baseio.slackclone.data.injection.repositoryModule
import dev.baseio.slackclone.data.injection.useCaseModule
import dev.baseio.slackclone.data.injection.viewModelModule
import dev.baseio.slackclone.navigation.*
import dev.baseio.slackclone.uichannels.createsearch.CreateNewChannelUI
import dev.baseio.slackclone.uichannels.createsearch.SearchCreateChannelUI
import dev.baseio.slackclone.uichat.newchat.NewChatThreadScreen
import dev.baseio.slackclone.uidashboard.compose.DashboardUI
import dev.baseio.slackclone.uionboarding.compose.EmailAddressInputUI
import dev.baseio.slackclone.uionboarding.compose.GettingStartedUI
import dev.baseio.slackclone.uionboarding.compose.SkipTypingUI
import dev.baseio.slackclone.uionboarding.compose.WorkspaceInputUI
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module


val appNavigator = SlackComposeNavigator()

@Composable
fun App(modifier: Modifier = Modifier, sqlDriver: SqlDriver) {
  initKoin(SlackDB.invoke(sqlDriver))
  Box(modifier) {
    Navigator(navigator = appNavigator, initialRoute = SlackScreens.OnboardingRoute) {
      this.route(SlackScreens.OnboardingRoute) {
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
      }
      this.route(SlackScreens.DashboardRoute) {
        screen(SlackScreens.Dashboard) {
          DashboardUI(this)
        }
        screen(SlackScreens.CreateChannelsScreen) {
          SearchCreateChannelUI(this)
        }
        screen(SlackScreens.CreateNewChannel) {
          CreateNewChannelUI(this)
        }
        screen(SlackScreens.CreateNewDM) {
          NewChatThreadScreen(this)
        }
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
