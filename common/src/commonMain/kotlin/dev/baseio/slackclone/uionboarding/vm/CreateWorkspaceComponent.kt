package dev.baseio.slackclone.uionboarding.vm

import dev.baseio.slackclone.uionboarding.compose.navigateDashboard
import dev.baseio.slackdomain.usecases.workspaces.UseCaseCreateWorkspace
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.arkivanov.decompose.ComponentContext
import dev.baseio.slackclone.uionboarding.coroutineScope
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import kotlinx.coroutines.SupervisorJob

class CreateWorkspaceComponent(
  componentContext: ComponentContext,
  private val useCaseCreateWorkspace: UseCaseCreateWorkspace,
  coroutineDispatcherProvider: CoroutineDispatcherProvider,
  private val login: Boolean,
  val navigateDashboard: () -> Unit
) : ComponentContext by componentContext {

  private val scope = coroutineScope(coroutineDispatcherProvider.main + SupervisorJob())

  val email = MutableStateFlow("")
  val password = MutableStateFlow("")
  val domain = MutableStateFlow("")
  val error = MutableStateFlow<Throwable?>(null)
  val loading = MutableStateFlow(false)
  fun createWorkspace() {
    scope.launch(CoroutineExceptionHandler { _, throwable ->
      throwable.printStackTrace()
      error.value = throwable
      loading.value = false
    }) {
      error.value = null
      loading.value = true
      val result = useCaseCreateWorkspace(email.value, password.value, domain.value)
      loading.value = false
      navigateDashboard()
    }
  }

  fun isLogin(): Boolean {
    return login
  }
}