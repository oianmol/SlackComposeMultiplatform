package dev.baseio.slackclone.uidashboard.home

import ViewModel
import dev.baseio.grpc.GrpcCalls
import dev.baseio.slackclone.appNavigator
import dev.baseio.slackclone.navigation.SlackScreens
import dev.baseio.slackdata.protos.KMSKUser
import dev.baseio.slackdomain.usecases.auth.UseCaseClearAuth
import dev.baseio.slackdomain.usecases.auth.UseCaseCurrentUser
import io.github.timortel.kotlin_multiplatform_grpc_lib.KMCode
import io.github.timortel.kotlin_multiplatform_grpc_lib.KMStatus
import io.github.timortel.kotlin_multiplatform_grpc_lib.KMStatusException
import io.grpc.StatusException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class UserProfileVM(
  private val useCaseCurrentUser: UseCaseCurrentUser,
  private val useCaseClearAuth: UseCaseClearAuth
) :
  ViewModel() {

  val currentLoggedInUser = MutableStateFlow<KMSKUser?>(null)

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