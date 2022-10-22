package dev.baseio.slackclone.uionboarding.vm

import dev.baseio.slackdomain.usecases.workspaces.UseCaseCreateWorkspace
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import dev.baseio.slackclone.koinApp
import dev.baseio.slackclone.uionboarding.coroutineScope
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import kotlinx.coroutines.SupervisorJob

class CreateWorkspaceComponent(
  componentContext: ComponentContext,
  private val login: Boolean,
  val navigateDashboard: () -> Unit
) : ComponentContext by componentContext {

  val createWorkspaceVM =
    instanceKeeper.getOrCreate {
      CreateWorkspaceVM(
        koinApp.koin.get(),
        navigateDashboard = navigateDashboard,
        useCaseCreateWorkspace = koinApp.koin.get()
      )
    }


  fun isLogin(): Boolean {
    return login
  }
}