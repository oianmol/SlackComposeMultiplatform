package dev.baseio.slackclone

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.squareup.sqldelight.db.SqlDriver
import dev.baseio.database.SlackDB
import dev.baseio.grpc.GrpcCalls
import dev.baseio.slackclone.data.injection.viewModelDelegateModule
import dev.baseio.slackclone.uidashboard.compose.DashboardUI
import dev.baseio.slackclone.uionboarding.compose.*
import dev.baseio.slackdata.SKKeyValueData
import dev.baseio.slackdata.injection.*
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module


lateinit var koinApp: KoinApplication

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun App(
  modifier: Modifier = Modifier,
  sqlDriver: SqlDriver,
  skKeyValueData: SKKeyValueData,
  defaultComponentContext: DefaultComponentContext
) {
  if (::koinApp.isInitialized.not()) {
    koinApp = initKoin(SlackDB.invoke(sqlDriver), skKeyValueData, defaultComponentContext)
  }


  val rootComponent = RootComponent(defaultComponentContext, koinApp.koin.get())
  Children(modifier = modifier, stack = rootComponent.childStack) {
    when (val child = it.instance) {
      is Root.Child.CreateWorkspace -> CreateWorkspaceScreen(child.component)
      is Root.Child.GettingStarted -> GettingStartedUI(child.component)
      is Root.Child.DashboardScreen -> DashboardUI(child.component, child.chatComponent)
    }
  }

  /* Box(modifier) {
     Navigator(navigator = appNavigator, initialRoute = initialRoute) {
       this.route(SlackScreens.OnboardingRoute) {
         screen(SlackScreens.GettingStarted) {
           val gettingStartedVM = scope.get<GettingStartedComponent>()
           GettingStartedUI(this@Navigator, gettingStartedVM)
         }
         screen(SlackScreens.CreateWorkspace) {
           val viewModel = scope.get<CreateWorkspaceComponent>().apply { navArgs = argMap }
           CreateWorkspaceScreen(this@Navigator, viewModel)
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
   }*/
}

fun initKoin(
  slackDB: SlackDB,
  skKeyValueData: SKKeyValueData,
  defaultComponentContext: DefaultComponentContext
): KoinApplication {
  return startKoin {
    modules(
      appModule(slackDB, skKeyValueData, defaultComponentContext),
      dataSourceModule,
      dataMappersModule,
      useCaseModule,
      viewModelDelegateModule,
      dispatcherModule
    )
  }
}

fun appModule(slackDB: SlackDB, skKeyValueData: SKKeyValueData, defaultComponentContext: DefaultComponentContext) =
  module {
    single { slackDB }
    single { skKeyValueData }
    single<ComponentContext> { defaultComponentContext }
    single { GrpcCalls(skKeyValueData = get(), address = "192.168.1.7") }
  }
