package dev.baseio.slackclone.uionboarding.vm

import dev.baseio.slackclone.navigation.ComposeNavigator
import dev.baseio.slackclone.uionboarding.compose.navigateDashboard
import dev.baseio.slackdomain.usecases.workspaces.UseCaseCreateWorkspace
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ViewModel
import dev.baseio.slackclone.navigation.SlackScreens

class WorkspaceCreateVM(private val useCaseCreateWorkspace: UseCaseCreateWorkspace) : ViewModel() {
  lateinit var navArgs: HashMap<String, Any>
  val email = MutableStateFlow("")
  val password = MutableStateFlow("")
  val domain = MutableStateFlow("")
  val error = MutableStateFlow<Throwable?>(null)
  val loading = MutableStateFlow(false)
  fun createWorkspace(composeNavigator: ComposeNavigator) {
    viewModelScope.launch(CoroutineExceptionHandler { coroutineContext, throwable ->
      throwable.printStackTrace()
      error.value = throwable
      loading.value = false
    }) {
      error.value = null
      loading.value = true
      val result = useCaseCreateWorkspace(email.value, password.value, domain.value)
      loading.value = false
      navigateDashboard(composeNavigator)
    }
  }

  fun isLogin(): Boolean {
    return navArgs[SlackScreens.CreateWorkspace.IS_LOGIN] == true
  }
}