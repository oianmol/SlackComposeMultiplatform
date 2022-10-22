package dev.baseio.slackclone.uidashboard.home

import dev.baseio.slackclone.uidashboard.vm.UserProfileDelegate
import com.arkivanov.decompose.ComponentContext
import dev.baseio.slackclone.uionboarding.coroutineScope
import dev.baseio.slackdomain.CoroutineDispatcherProvider
import kotlinx.coroutines.SupervisorJob

class UserProfileComponent(
  private val userProfileDelegate: UserProfileDelegate,
  coroutineDispatcherProvider: CoroutineDispatcherProvider,
  componentContext: ComponentContext,
  val navigateOnboardingRoot: () -> Unit
) :
  UserProfileDelegate by userProfileDelegate, ComponentContext by componentContext {
  private val scope = coroutineScope(coroutineDispatcherProvider.main + SupervisorJob())

  init {
    getCurrentUser(scope, navigateOnboardingRoot)
  }
}