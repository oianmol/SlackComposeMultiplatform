package dev.baseio.slackclone.uidashboard.home

import ViewModel
import dev.baseio.grpc.GrpcCalls
import dev.baseio.slackclone.appNavigator
import dev.baseio.slackclone.navigation.SlackScreens
import dev.baseio.slackdata.protos.KMSKUser
import io.github.timortel.kotlin_multiplatform_grpc_lib.KMCode
import io.github.timortel.kotlin_multiplatform_grpc_lib.KMStatus
import io.github.timortel.kotlin_multiplatform_grpc_lib.KMStatusException
import io.grpc.StatusException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class UserProfileVM(private val grpcCalls: GrpcCalls) : ViewModel() {

  val currentLoggedInUser = MutableStateFlow<KMSKUser?>(null)

  fun fetchUserProfile() {
    viewModelScope.launch(CoroutineExceptionHandler { coroutineContext, throwable ->
      if (throwable is KMStatusException && throwable.status.code == KMCode.UNAUTHENTICATED) {
        grpcCalls.clearAuth()
        appNavigator.navigateRoute(SlackScreens.OnboardingRoute, clearRoutes = { route, remove ->
          if (route.name == SlackScreens.DashboardRoute.name) {
            remove()
          }
        })
      }
    }) {
      val user = grpcCalls.currentLoggedInUser()
      currentLoggedInUser.value = user
    }
  }

}