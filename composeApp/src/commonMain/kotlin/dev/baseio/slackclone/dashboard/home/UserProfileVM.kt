package dev.baseio.slackclone.dashboard.home

import dev.baseio.slackclone.SlackViewModel
import dev.baseio.slackclone.dashboard.vm.UserProfileDelegate
import dev.baseio.slackdomain.CoroutineDispatcherProvider

class UserProfileVM(
    private val userProfileDelegate: UserProfileDelegate,
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
    navigateOnboardingRoot: () -> Unit
) : SlackViewModel(coroutineDispatcherProvider), UserProfileDelegate by userProfileDelegate {
    init {
        getCurrentUser(viewModelScope, navigateOnboardingRoot)
    }
}
