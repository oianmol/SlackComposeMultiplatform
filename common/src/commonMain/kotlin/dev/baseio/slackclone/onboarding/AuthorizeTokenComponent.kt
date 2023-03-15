package dev.baseio.slackclone.onboarding

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import dev.baseio.slackclone.getKoin

class AuthorizeTokenComponent(
    componentContext: ComponentContext,
    val navigateBack: () -> Unit,
    private val navigateDashboard: () -> Unit,
    private val token: String
) : ComponentContext by componentContext {

    val viewModel =
        instanceKeeper.getOrCreate {
            AuthorizeTokenVM(
                coroutineDispatcherProvider = getKoin().get(),
                useCaseFetchAndSaveCurrentUser = getKoin().get(),
                useCaseFetchAndSaveUserWorkspace = getKoin().get(),
                token = token,
                navigateBackNow = navigateBack,
                navigateDashboard = navigateDashboard
            )
        }
}