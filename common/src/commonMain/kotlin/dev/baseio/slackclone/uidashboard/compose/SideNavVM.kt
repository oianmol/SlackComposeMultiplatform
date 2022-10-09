package dev.baseio.slackclone.uidashboard.compose

import ViewModel
import dev.baseio.slackclone.appNavigator
import dev.baseio.slackclone.navigation.SlackScreens
import dev.baseio.slackdata.protos.KMSKUser
import dev.baseio.slackdomain.model.workspaces.DomainLayerWorkspaces
import dev.baseio.slackdomain.usecases.auth.UseCaseClearAuth
import dev.baseio.slackdomain.usecases.auth.UseCaseCurrentUser
import dev.baseio.slackdomain.usecases.workspaces.UseCaseSetLastSelectedWorkspace
import dev.baseio.slackdomain.usecases.workspaces.UseCaseFetchWorkspaces
import io.github.timortel.kotlin_multiplatform_grpc_lib.KMCode
import io.github.timortel.kotlin_multiplatform_grpc_lib.KMStatusException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SideNavVM(
  private val useCaseFetchWorkspaces: UseCaseFetchWorkspaces,
  private val useCaseLastSelectedWorkspace: UseCaseSetLastSelectedWorkspace,
  private val useCaseCurrentUser: UseCaseCurrentUser,
  private val useCaseClearAuth: UseCaseClearAuth
) : ViewModel() {
  val currentLoggedInUser = MutableStateFlow<KMSKUser?>(null)

  var workspacesFlow = MutableStateFlow(flow())
    private set

  init {
    fetchUserProfile()
  }

  fun flow(): Flow<List<DomainLayerWorkspaces.SKWorkspace>> {
    return useCaseFetchWorkspaces.invoke()
  }

  fun select(skWorkspace: DomainLayerWorkspaces.SKWorkspace) {
    viewModelScope.launch {
      useCaseLastSelectedWorkspace.invoke(skWorkspace)
    }
  }



  fun fetchUserProfile() {
    viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
      if (throwable is KMStatusException && throwable.status.code == KMCode.UNAUTHENTICATED) {
        useCaseClearAuth()
        appNavigator.navigateRoute(SlackScreens.OnboardingRoute, clearRoutes = { route, remove ->
          if (route.name == SlackScreens.DashboardRoute.name) {
            remove()
          }
        })
      }
    }) {
      val result = useCaseCurrentUser()
      currentLoggedInUser.value = result.getOrThrow()
    }
  }
}