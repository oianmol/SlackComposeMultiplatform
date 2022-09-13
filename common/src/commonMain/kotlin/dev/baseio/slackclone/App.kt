package dev.baseio.slackclone

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.squareup.sqldelight.db.SqlDriver
import dev.baseio.FakeDataPreloader
import dev.baseio.database.SlackDB
import dev.baseio.slackclone.chatcore.injection.uiModelMapperModule
import dev.baseio.slackclone.data.injection.viewModelModule
import dev.baseio.slackclone.injection.SlackComponent
import dev.baseio.slackclone.navigation.*
import dev.baseio.slackclone.uichannels.createsearch.CreateNewChannelUI
import dev.baseio.slackclone.uichannels.createsearch.SearchCreateChannelUI
import dev.baseio.slackclone.uichat.newchat.NewChatThreadScreen
import dev.baseio.slackclone.uidashboard.compose.DashboardUI
import dev.baseio.slackclone.uionboarding.compose.EmailAddressInputUI
import dev.baseio.slackclone.uionboarding.compose.GettingStartedUI
import dev.baseio.slackclone.uionboarding.compose.SkipTypingUI
import dev.baseio.slackclone.uionboarding.compose.WorkspaceInputUI
import dev.baseio.slackdata.injection.dataMappersModule
import dev.baseio.slackdata.injection.dataSourceModule
import dev.baseio.slackdata.injection.dispatcherModule
import dev.baseio.slackdata.injection.useCaseModule
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module


lateinit var slackComponent: SlackComponent
var koinApp: KoinApplication? = null
val appNavigator = SlackComposeNavigator()

@Composable
fun App(modifier: Modifier = Modifier, sqlDriver: SqlDriver) {
  if (koinApp == null) {
    koinApp = initKoin(SlackDB.invoke(sqlDriver))
    slackComponent = SlackComponent()
    GlobalScope.launch {
      // dirty! just for sample demo
      koinApp?.koin?.get<FakeDataPreloader>()?.preload()
    }
  }

  Box(modifier) {
    Navigator(navigator = appNavigator, initialRoute = SlackScreens.OnboardingRoute) {
      this.route(SlackScreens.OnboardingRoute) {
        screen(SlackScreens.GettingStarted) {
          GettingStartedUI(this, slackComponent.provideGettingStartedVM())
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
          DashboardUI(this, slackComponent.provideDashboardVM(), slackComponent.provideChatScreenVM())
        }
        screen(SlackScreens.CreateChannelsScreen) {
          SearchCreateChannelUI(this, slackComponent.provideSearchChannelsVM())
        }
        screen(SlackScreens.CreateNewChannel) {
          CreateNewChannelUI(this, slackComponent.provideCreateChannelVM())
        }
        screen(SlackScreens.CreateNewDM) {
          NewChatThreadScreen(this, slackComponent.provideNewChatThreadVM())
        }
      }
    }
  }
}

fun initKoin(slackDB: SlackDB): KoinApplication {
  return startKoin {
    modules(module {
      single { slackDB }
      single { FakeDataPreloader(get(), get(), get(), get()) }
    }, dataSourceModule, dataMappersModule, useCaseModule, viewModelModule, uiModelMapperModule, dispatcherModule)
  }
}
