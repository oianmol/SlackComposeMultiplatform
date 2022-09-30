package dev.baseio.slackclone

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.squareup.sqldelight.db.SqlDriver
import dev.baseio.database.SlackDB
import dev.baseio.getWorkspaces
import dev.baseio.slackclone.chatcore.injection.uiModelMapperModule
import dev.baseio.slackclone.data.injection.viewModelModule
import dev.baseio.slackclone.navigation.*
import dev.baseio.slackclone.uichannels.createsearch.CreateNewChannelUI
import dev.baseio.slackclone.uichannels.createsearch.SearchCreateChannelUI
import dev.baseio.slackclone.uichat.newchat.NewChatThreadScreen
import dev.baseio.slackclone.uidashboard.compose.DashboardUI
import dev.baseio.slackclone.uionboarding.GettingStartedVM
import dev.baseio.slackclone.uionboarding.compose.EmailAddressInputUI
import dev.baseio.slackclone.uionboarding.compose.GettingStartedUI
import dev.baseio.slackclone.uionboarding.compose.SkipTypingUI
import dev.baseio.slackclone.uionboarding.compose.WorkspaceInputUI
import dev.baseio.slackdata.injection.*
import kotlinx.coroutines.flow.collectLatest
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module


val appNavigator = SlackComposeNavigator()
var koinApp: KoinApplication? = null

@Composable
fun App(modifier: Modifier = Modifier, sqlDriver: SqlDriver) {
  if (koinApp == null) {
    koinApp = initKoin(SlackDB.invoke(sqlDriver))
  }
  LaunchedEffect(true) {
    getWorkspaces().collectLatest {
      it.workspacesList.forEach {
        println("${it.name} ${it.domain} ${it.uuid}")

      }
    }
  }

  Box(modifier) {
    Navigator(navigator = appNavigator, initialRoute = SlackScreens.OnboardingRoute) {
      this.route(SlackScreens.OnboardingRoute) {
        screen(SlackScreens.GettingStarted) {
          val gettingStartedVM = scope.get<GettingStartedVM>()
          GettingStartedUI(this@Navigator, gettingStartedVM)
        }
        screen(SlackScreens.SkipTypingScreen) {
          SkipTypingUI(this@Navigator)
        }
        screen(SlackScreens.WorkspaceInputUI) {
          WorkspaceInputUI(this@Navigator)
        }
        screen(SlackScreens.EmailAddressInputUI) {
          EmailAddressInputUI(this@Navigator)
        }
      }
      this.route(SlackScreens.DashboardRoute) {
        screen(SlackScreens.Dashboard) {
          DashboardUI(this@Navigator, scope.get(), scope.get())
        }
        screen(SlackScreens.CreateChannelsScreen) {
          SearchCreateChannelUI(this@Navigator, scope.get())
        }
        screen(SlackScreens.CreateNewChannel) {
          CreateNewChannelUI(this@Navigator, scope.get())
        }
        screen(SlackScreens.CreateNewDM) {
          NewChatThreadScreen(this@Navigator, scope.get())
        }
      }
    }
  }
}

fun initKoin(slackDB: SlackDB): KoinApplication {
  return startKoin {
    modules(
      appModule(slackDB),
      dataSourceModule,
      dataMappersModule,
      useCaseModule,
      viewModelModule,
      uiModelMapperModule,
      dispatcherModule
    )
  }
}

fun appModule(slackDB: SlackDB) =
  module {
    single { slackDB }
  }
