package dev.baseio.slackclone.uidashboard.home

import dev.baseio.slackclone.SlackViewModel
import dev.baseio.slackclone.uidashboard.vm.UserProfileDelegate
import dev.baseio.slackdomain.CoroutineDispatcherProvider

class UserProfileVM(
    private val userProfileDelegate: UserProfileDelegate,
    coroutineDispatcherProvider: CoroutineDispatcherProvider,
    navigateOnboardingRoot: () -> Unit
) : SlackViewModel(), UserProfileDelegate by userProfileDelegate {
    init {
        getCurrentUser(viewModelScope, navigateOnboardingRoot)
    }
}
