package dev.baseio.slackclone.dashboard.vm

import dev.baseio.slackdomain.model.users.DomainLayerUsers
import dev.baseio.slackdomain.usecases.auth.UseCaseFetchAndSaveCurrentUser
import dev.baseio.slackdomain.usecases.auth.UseCaseLogout
import io.github.timortel.kotlin_multiplatform_grpc_lib.KMCode
import io.github.timortel.kotlin_multiplatform_grpc_lib.KMStatusException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

interface UserProfileDelegate {
    val currentLoggedInUser: MutableStateFlow<DomainLayerUsers.SKUser?>
    fun getCurrentUser(viewModelScope: CoroutineScope, navigateOnboardingRoot: () -> Unit)
    fun logout()
}

class UserProfileDelegateImpl(
    private val useCaseFetchAndSaveCurrentUser: UseCaseFetchAndSaveCurrentUser,
    private val useCaseClearAuth: UseCaseLogout
) : UserProfileDelegate {

    override val currentLoggedInUser: MutableStateFlow<DomainLayerUsers.SKUser?> = MutableStateFlow(null)

    override fun logout() {
        useCaseClearAuth()
    }

    override fun getCurrentUser(viewModelScope: CoroutineScope, navigateOnboardingRoot: () -> Unit) {
        viewModelScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                when {
                    throwable is KMStatusException && throwable.status.code == KMCode.UNAUTHENTICATED -> {
                        useCaseClearAuth()
                        navigateOnboardingRoot()
                    }
                }
            }
        ) {
            val result = useCaseFetchAndSaveCurrentUser()
            currentLoggedInUser.value = result.getOrThrow()
        }
    }
}
