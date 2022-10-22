package dev.baseio.slackclone

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import dev.baseio.slackclone.uichat.chatthread.ChatScreenComponent
import dev.baseio.slackclone.uidashboard.vm.DashboardComponent
import dev.baseio.slackclone.uionboarding.GettingStartedComponent
import dev.baseio.slackclone.uionboarding.vm.CreateWorkspaceComponent
import dev.baseio.slackdata.SKKeyValueData
import dev.baseio.slackdomain.AUTH_TOKEN

interface Root {
  val childStack: Value<ChildStack<*, Child>>

  fun navigateCreateWorkspace(isLogin: Boolean)
  fun navigateDashboard()
  sealed class Child {
    data class GettingStarted(val component: GettingStartedComponent) : Child()
    data class CreateWorkspace(val component: CreateWorkspaceComponent) : Child()
    data class DashboardScreen(val component: DashboardComponent, val chatComponent: ChatScreenComponent) : Child()
  }
}

class RootComponent(
  context: ComponentContext,
  skKeyValueData: SKKeyValueData
) : Root, ComponentContext by context {

  private val navigation = StackNavigation<Config>()

  override val childStack: Value<ChildStack<*, Root.Child>> = childStack(
    source = navigation,
    initialConfiguration = skKeyValueData.get(AUTH_TOKEN)?.let {
      Config.DashboardScreen
    } ?: run {
      Config.GettingStarted
    },
    handleBackButton = true, // Pop the back stack on back button press
    childFactory = ::createChild,
  )

  override fun navigateCreateWorkspace(isLogin: Boolean) {
    navigation.push(Config.CreateWorkspace(isLogin))
  }

  override fun navigateDashboard() {
    navigation.push(Config.DashboardScreen)
  }

  private fun createChild(config: Config, componentContext: ComponentContext): Root.Child =
    when (config) {
      is Config.GettingStarted -> Root.Child.GettingStarted(
        GettingStartedComponent(
          componentContext,
          koinApp.koin.get()
        ) { isLogin ->
          navigateCreateWorkspace(isLogin)
        }
      )

      is Config.CreateWorkspace -> Root.Child.CreateWorkspace(
        CreateWorkspaceComponent(
          componentContext,
          koinApp.koin.get(),
          koinApp.koin.get(),
          config.isLogin
        ) {
          navigateDashboard()
        }
      )

      Config.DashboardScreen -> Root.Child.DashboardScreen(
        DashboardComponent(
          componentContext,
          koinApp.koin.get(),
          koinApp.koin.get(),
          koinApp.koin.get(),
          koinApp.koin.get(),
          koinApp.koin.get(),
          koinApp.koin.get(),
          koinApp.koin.get(),
          koinApp.koin.get(),
          koinApp.koin.get(),
          koinApp.koin.get()
        ),
        ChatScreenComponent(
          componentContext, koinApp.koin.get(),
          koinApp.koin.get(),
          koinApp.koin.get(),
          koinApp.koin.get(),
          koinApp.koin.get(),
          koinApp.koin.get(),
        )
      )
    }

  private sealed class Config : Parcelable {

    @Parcelize
    object GettingStarted : Config()

    @Parcelize
    object DashboardScreen : Config()

    @Parcelize
    data class CreateWorkspace(var isLogin: Boolean) : Config()
  }
}