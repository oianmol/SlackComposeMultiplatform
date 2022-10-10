package dev.baseio.slackclone

import SKKeyValueData
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.squareup.sqldelight.db.SqlDriver
import dev.baseio.database.SlackDB
import dev.baseio.grpc.GrpcCalls
import dev.baseio.slackclone.chatcore.injection.uiModelMapperModule
import dev.baseio.slackclone.data.injection.viewModelDelegateModule
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
import dev.baseio.slackclone.uionboarding.vm.EmailInputVM
import dev.baseio.slackclone.uionboarding.vm.WorkspaceInputVM
import dev.baseio.slackdata.injection.*
import dev.baseio.slackdomain.AUTH_TOKEN
import dev.baseio.slackdomain.datasources.local.workspaces.SKLocalDataSourceWriteWorkspaces
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module


val appNavigator = SlackComposeNavigator()
var koinApp: KoinApplication? = null

@Composable
fun App(modifier: Modifier = Modifier, sqlDriver: SqlDriver, skKeyValueData: SKKeyValueData) {
  if (koinApp == null) {
    koinApp = initKoin(SlackDB.invoke(sqlDriver), skKeyValueData)
  }
  val initialRoute = skKeyValueData.get(AUTH_TOKEN)?.let {
    SlackScreens.DashboardRoute
  } ?: run {
    SlackScreens.OnboardingRoute
  }

  Box(modifier) {
    Navigator(navigator = appNavigator, initialRoute = initialRoute) {
      this.route(SlackScreens.OnboardingRoute) {
        screen(SlackScreens.GettingStarted) {
          val gettingStartedVM = scope.get<GettingStartedVM>()
          GettingStartedUI(this@Navigator, gettingStartedVM)
        }
        screen(SlackScreens.SkipTypingScreen) {
          SkipTypingUI(this@Navigator)
        }
        screen(SlackScreens.WorkspaceInputUI) {
          val workspaceInputVM = scope.get<WorkspaceInputVM>()
          WorkspaceInputUI(this@Navigator, workspaceInputVM)
        }
        screen(SlackScreens.EmailAddressInputUI) {
          val emailInputVM = scope.get<EmailInputVM>()
          EmailAddressInputUI(this@Navigator, emailInputVM)
        }
      }
      this.route(SlackScreens.WorkspaceSigninRoute) {
        screen(SlackScreens.EmailAddressInputUI) {
          val emailInputVM = scope.get<EmailInputVM>()
          EmailAddressInputUI(this@Navigator, emailInputVM)
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

fun initKoin(slackDB: SlackDB, skKeyValueData: SKKeyValueData): KoinApplication {
  return startKoin {
    modules(
      appModule(slackDB, skKeyValueData),
      dataSourceModule,
      dataMappersModule,
      useCaseModule,
      viewModelModule,
      viewModelDelegateModule,
      uiModelMapperModule,
      dispatcherModule
    )
  }
}

fun appModule(slackDB: SlackDB, skKeyValueData: SKKeyValueData) =
  module {
    single { slackDB }
    single { skKeyValueData }
    single { GrpcCalls(get()) }
  }
